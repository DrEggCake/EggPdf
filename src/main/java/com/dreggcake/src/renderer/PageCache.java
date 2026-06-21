package com.dreggcake.src.renderer;

import com.dreggcake.src.pdf.core.PageManager;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

public class PageCache {

    private static final int MAX_CACHE_SIZE = 16;

    private final ExecutorService threadPool;

    private final LinkedHashMap<Integer, Integer> textures =
            new LinkedHashMap<>(4, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    if (size() <= MAX_CACHE_SIZE)
                        return false;

                    GL11.glDeleteTextures(eldest.getValue());
                    return true;
                }
            };

    private final Map<Integer, CompletableFuture<BufferedImage>> pending =
            new HashMap<>();

    private final PageManager pageManager;

    public PageCache(PageManager pageManager, ExecutorService threadPool) {
        this.pageManager = pageManager;
        this.threadPool = threadPool;
    }

    /**
     * Returns the texture ID if available.
     * Returns null if still rendering or not requested.
     */
    public Integer get(RenderPage page) {

        int index = page.getPage().getIndex();

        Integer texture = textures.get(index);
        if (texture != null)
            return texture;

        CompletableFuture<BufferedImage> future = pending.get(index);

        if (future == null) {
            request(page);
            return null;
        }

        if (!future.isDone())
            return null;

        BufferedImage image = future.join();
        pending.remove(index);

        texture = TextureLoader.loadTexture(image);

        image.flush();

        textures.put(index, texture);

        return texture;
    }

    /**
     * Begins rendering if not already cached.
     */
    public void request(RenderPage page) {

        int index = page.getPage().getIndex();

        if (textures.containsKey(index))
            return;

        if (pending.containsKey(index))
            return;

        pending.put(
                index,
                CompletableFuture.supplyAsync(
                        () -> pageManager.getDocument().renderPage(index, 1.5f),
                        threadPool
                )
        );
    }

    public void clear() {

        for (Integer texture : textures.values())
            GL11.glDeleteTextures(texture);

        textures.clear();

        pending.values().forEach(f -> f.cancel(true));
        pending.clear();
    }

    public void shutdown() {
        clear();
        threadPool.shutdown();
    }
}