package com.dreggcake.src.renderer;

import com.dreggcake.src.app.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Renderer {

    Shader shader;
    int tex;
    int VAO;
    int VBO;

    public void start(Window win){

        init(win);
        run(win.window);

    }

    public void init(Window win){
        float[] vertices = {
                // positions          // texture coords
                // top left
                -0.5f,  0.5f, 0.0f,   0.0f, 1.0f,
                // bottom right
                0.5f, -0.5f, 0.0f,   1.0f, 0.0f,
                // top right
                0.5f,  0.5f, 0.0f,   1.0f, 1.0f,

                // top left
                -0.5f,  0.5f, 0.0f,   0.0f, 1.0f,
                // bottom left
                -0.5f, -0.5f, 0.0f,   0.0f, 0.0f,
                // bottom right
                0.5f, -0.5f, 0.0f,   1.0f, 0.0f,
        };

        VBO = GL15.glGenBuffers();
        VAO = GL30.glGenVertexArrays();

        GL30.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        int stride = 5 * Float.BYTES;

        GL20.glVertexAttribPointer(
                0,
                3,
                GL11.GL_FLOAT,
                false,
                stride,
                0
        );
        GL20.glEnableVertexAttribArray(0);


        GL20.glVertexAttribPointer(
                1,
                2,
                GL11.GL_FLOAT,
                false,
                stride,
                3 * Float.BYTES
        );
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);

        MemoryUtil.memFree(vertexBuffer);

        shader = new Shader(
                new Shader.ShaderSource(
                        Shader.ShaderType.VERTEX,
                        "/shaders/shader.vert"
                ),
                new Shader.ShaderSource(
                        Shader.ShaderType.FRAGMENT,
                        "/shaders/shader.frag"
                )
        );

        shader.use();
        shader.setInt("tex", 0);

        tex = loadTexture("/textures/img.png");


    }

    public void run(long window) {
        while (!GLFW.glfwWindowShouldClose(window)) {
            draw();
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void draw() {
        GL11.glClearColor(0.0f / 255.0f, 25.0f / 255.0f, 53 / 255.0f, 0.8f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shader.use();

        GL30.glBindVertexArray(VAO);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, tex);


        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0,
                6);

    }


    private int loadTexture(String resourcePath) {

        int texture = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        // Wrapping
        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_WRAP_S,
                GL11.GL_REPEAT
        );

        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_WRAP_T,
                GL11.GL_REPEAT
        );

        // Filtering
        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_NEAREST
        );

        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D,
                GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_NEAREST
        );

        try (
                MemoryStack stack = MemoryStack.stackPush();
                InputStream is =
                        Renderer.class.getResourceAsStream(resourcePath)
        ) {

            if (is == null) {
                throw new RuntimeException(
                        "Texture resource not found: "
                                + resourcePath
                );
            }

            // Read resource into byte[]
            byte[] bytes = is.readAllBytes();

            // Convert to ByteBuffer
            ByteBuffer imageBuffer =
                    MemoryUtil.memAlloc(bytes.length);

            imageBuffer.put(bytes);
            imageBuffer.flip();

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);

            ByteBuffer data = STBImage.stbi_load_from_memory(
                    imageBuffer,
                    width,
                    height,
                    channels,
                    0
            );

            MemoryUtil.memFree(imageBuffer);

            if (data == null) {
                throw new RuntimeException(
                        "Failed to load texture: "
                                + STBImage.stbi_failure_reason()
                );
            }

            int format;

            if (channels.get(0) == 3) {
                format = GL11.GL_RGB;
            }
            else if (channels.get(0) == 4) {
                format = GL11.GL_RGBA;
            }
            else {
                throw new RuntimeException(
                        "Unsupported image format"
                );
            }

            GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    format,
                    width.get(0),
                    height.get(0),
                    0,
                    format,
                    GL11.GL_UNSIGNED_BYTE,
                    data
            );

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            STBImage.stbi_image_free(data);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load texture resource: "
                            + resourcePath,
                    e
            );
        }

        return texture;
    }

}
