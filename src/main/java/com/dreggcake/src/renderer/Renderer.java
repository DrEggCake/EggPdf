package com.dreggcake.src.renderer;

import com.dreggcake.src.app.Window;
import com.dreggcake.src.pdf.core.PDFDocument;
import com.dreggcake.src.pdf.core.PDFLoader;
import com.dreggcake.src.pdf.core.PageManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Renderer {

    Shader shader;
    int VAO;
    int VBO;

    PageManager pageManager;
    Camera camera = new Camera();

    Matrix4f projection = new Matrix4f();
    Matrix4f model = new Matrix4f();
    float SCALE = 0.0025f;

    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(1, cores - 1));


    public void start(Window win) {

        init(win);
        run(win.window);

    }

    public void init(Window window) {
        float[] vertices = {
                // positions          // texture coords
                // top left
                -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                // bottom right
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                // top right
                0.5f, 0.5f, 0.0f, 1.0f, 1.0f,

                // top left
                -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
                // bottom left
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                // bottom right
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
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

        projection.identity().ortho(
                -window.aspectRatio, window.aspectRatio,
                -1f, 1f,
                -1f, 1f
        );

        // will be removed after basic ui is implemented
        File pdfFile = choosePDFFile();
        PDFDocument document;

        if (pdfFile == null) {
            document = PDFLoader.loadDocumentFromResource("/pdf/test.pdf");
            window.setTitle("EggPDF - No PDF selected - Displaying Sample PDF");
        } else {
            document = PDFLoader.loadDocument(pdfFile.getAbsolutePath());
            window.setTitle("EggPDF - " + pdfFile.getName());
        }


        pageManager = new PageManager(document);
    }

    public void run(long window) {
        while (!GLFW.glfwWindowShouldClose(window)) {
            input(window);

            draw();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
        threadPool.shutdown();
    }

    private void draw() {
        GL11.glClearColor(0.0f / 255.0f, 25.0f / 255.0f, 53 / 255.0f, 0.8f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shader.use();
        shader.setMat4("projection", projection);
        shader.setMat4("view", camera.getView());


        GL30.glBindVertexArray(VAO);

        for (RenderPage page : pageManager.getVisiblePages(camera)) {
            if (!page.loaded && page.future == null) {
                page.future = CompletableFuture.supplyAsync(() ->
                                pageManager.getDocument().renderPage(
                                        page.getPage().getIndex(), 1.5f),
                        threadPool
                );
            }

            if (!page.loaded && page.future.isDone()) {
                BufferedImage image = page.future.join();
                page.texture = loadTexture(image);
                page.loaded = true;
                page.future = null;
            }

            if (!page.loaded) continue;

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, page.texture);

            float width = page.getPage().getWidth() * SCALE;
            float height = page.getPage().getHeight() * SCALE;

            model.identity()
                    .translate(page.x, page.y, 0)
                    .scale(width, height, 1.0f);

            shader.setMat4("model", model);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        }

    }

    private void input(long window) {

        // scroll
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W)
                == GLFW.GLFW_PRESS) {
            camera.y += 0.05f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S)
                == GLFW.GLFW_PRESS) {
            camera.y -= 0.05f;
        }

        // zoom
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP)
                == GLFW.GLFW_PRESS) {
            camera.zoom += 0.01f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN)
                == GLFW.GLFW_PRESS) {
            camera.zoom -= 0.01f;
        }

        // clamp zoom
        camera.zoom = Math.max(0.2f,
                Math.min(3.0f, camera.zoom));
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

            byte[] bytes = is.readAllBytes();

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
            } else if (channels.get(0) == 4) {
                format = GL11.GL_RGBA;
            } else {
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

    // overloaded ( for now ) because we already have the image in BufferedImage
    private int loadTexture(BufferedImage image) {

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

        ByteBuffer buffer =
                MemoryUtil.memAlloc(width * height * 4);

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

        GL30.glGenerateMipmap(
                GL11.GL_TEXTURE_2D
        );

        MemoryUtil.memFree(buffer);

        return texture;
    }


    // a VERY temporary solution to load PDFs other than the sample pdf (in resources/pdf/test.pdf)
    private File choosePDFFile() {
        FileDialog dialog = new FileDialog(
                (Frame) null, "Select a PDF File", FileDialog.LOAD
        );

        dialog.setFile("*.pdf");
        dialog.setVisible(true);

        String directory = dialog.getDirectory();
        String file = dialog.getFile();

        dialog.dispose();

        if (directory == null || file == null) {
            return null;
        }
        return new File(directory, file);

    }


}
