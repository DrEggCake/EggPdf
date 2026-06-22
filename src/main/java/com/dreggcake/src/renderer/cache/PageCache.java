package com.dreggcake.src.renderer.cache;

import com.dreggcake.src.pdf.core.PageManager;
import com.dreggcake.src.renderer.RenderPage;
import com.dreggcake.src.renderer.TextureLoader;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

import static com.dreggcake.src.renderer.cache.CacheKey.getScaleBucket;

public class PageCache {

    private static final int MAX_CACHE_SIZE = 16;

    private final ExecutorService threadPool;

    private final LinkedHashMap<CacheKey, Integer> textures =
            new LinkedHashMap<>(4, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<CacheKey, Integer> eldest) {
                    if (size() <= MAX_CACHE_SIZE)
                        return false;

                    GL11.glDeleteTextures(eldest.getValue());
                    return true;
                }
            };

    private final Map<CacheKey, CompletableFuture<BufferedImage>> pending =
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
    public Integer get(RenderPage page, float zoom) {

        float bucket = getScaleBucket(zoom);
        int pageIndex = page.getPage().getIndex();

        CacheKey key = new CacheKey(pageIndex, bucket);

        // Exact texture already cached.
        Integer texture = textures.get(key);
        if (texture != null)
            return texture;

        CompletableFuture<BufferedImage> future = pending.get(key);

        if (future == null) {
            request(page, zoom);
            future = pending.get(key);
        }

        if (!future.isDone()) {
            // use whatever texture is available till the correct resolution
            // finishes rendering (prevents pages disappearing from screen for split second)
            texture = findBestTexture(pageIndex);
            return texture; // may be null if this page has never been rendered
        }

        // Rendering finished.
        BufferedImage image = future.join();
        pending.remove(key);

        texture = TextureLoader.loadTexture(image);
        image.flush();

        textures.put(key, texture);

        return texture;
    }

    /**
     * Begins rendering if not already cached.
     */
    public void request(RenderPage page, float zoom) {

        float bucket = getScaleBucket(zoom);
        CacheKey key = new CacheKey(page.getPage().getIndex(), bucket);

        if (textures.containsKey(key))
            return;

        if (pending.containsKey(key))
            return;

        pending.put(
                key,
                CompletableFuture.supplyAsync(
                        () -> pageManager.getDocument().renderPage(
                                page.getPage().getIndex(),
                                bucket / 2.0f
                        ),
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

    private Integer findBestTexture(int pageIndex) {

        for (Map.Entry<CacheKey, Integer> entry : textures.entrySet()) {
            if (entry.getKey().pageIndex() == pageIndex)
                return entry.getValue();
        }

        return null;
    }
}