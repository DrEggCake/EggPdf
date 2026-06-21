package com.dreggcake.src.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class TextureLoader {

    private static ByteBuffer textureBuffer;

    public static int loadTexture(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];

        image.getRGB(
                0,
                0,
                width,
                height,
                pixels,
                0,
                width
        );

        int requiredSize = width * height * 4;

        if (textureBuffer == null || textureBuffer.capacity() < requiredSize) {

            if (textureBuffer != null) {
                MemoryUtil.memFree(textureBuffer);
            }

            textureBuffer = MemoryUtil.memAlloc(requiredSize);

        }
        textureBuffer.clear();

        ByteBuffer buffer = textureBuffer;

        // Convert ARGB -> RGBA
        for (int y = height - 1; y >= 0; y--) {

            for (int x = 0; x < width; x++) {

                int pixel =
                        pixels[y * width + x];

                buffer.put(
                        (byte) ((pixel >> 16) & 0xFF)
                ); // R

                buffer.put(
                        (byte) ((pixel >> 8) & 0xFF)
                ); // G

                buffer.put(
                        (byte) (pixel & 0xFF)
                ); // B

                buffer.put(
                        (byte) ((pixel >> 24) & 0xFF)
                ); // A
            }
        }

        buffer.flip();

        int texture = GL11.glGenTextures();

        GL11.glBindTexture(
                GL11.GL_TEXTURE_2D,
                texture
        );

        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR
        );

        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR
        );

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA8,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buffer
        );

        return texture;
    }


}
