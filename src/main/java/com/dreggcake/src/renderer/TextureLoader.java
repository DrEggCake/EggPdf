package com.dreggcake.src.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureLoader {

    private static int placeholderTexture = -1;

    /**
     * Converts a BufferedImage to an RGBA ByteBuffer using bulk IntBuffer writes.
     * Must be called OFF the GL thread (background thread).
     */
    public static ReadyTexture convertToBuffer(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        IntBuffer intBuf = buffer.asIntBuffer();

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                int a = (pixel >> 24) & 0xFF;

                // Pack as little-endian RGBA: 0xAABBGGRR
                intBuf.put((a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        buffer.flip();
        image.flush();

        return new ReadyTexture(buffer, width, height);
    }

    /**
     * Uploads a pre-converted ReadyTexture to the GPU.
     * Must be called ON the GL thread. Frees the buffer after upload.
     */
    public static int uploadTexture(ReadyTexture ready) {
        int texture = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
                ready.width(), ready.height(), 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, ready.buffer()
        );

        MemoryUtil.memFree(ready.buffer());

        return texture;
    }

    /**
     * Returns a 1x1 white placeholder texture for unrendered pages.
     * Must be called on the GL thread.
     */
    public static int getPlaceholderTexture() {
        if (placeholderTexture != -1)
            return placeholderTexture;

        ByteBuffer buf = MemoryUtil.memAlloc(4);
        buf.put((byte) 0xFF)
                .put((byte) 0xFF)
                .put((byte) 0xFF)
                .put((byte) 0xFF);
        buf.flip();

        placeholderTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, placeholderTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
                1, 1, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf
        );

        MemoryUtil.memFree(buf);

        return placeholderTexture;
    }
}
