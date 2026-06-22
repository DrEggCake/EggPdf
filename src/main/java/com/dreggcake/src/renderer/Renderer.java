package com.dreggcake.src.renderer;

import com.dreggcake.src.app.Window;
import com.dreggcake.src.pdf.core.PDFDocument;
import com.dreggcake.src.pdf.core.PDFLoader;
import com.dreggcake.src.pdf.core.PageManager;
import com.dreggcake.src.renderer.cache.PageCache;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Renderer {

    Shader shader;
    int VAO;
    int VBO;

    PageManager pageManager;
    PageCache pageCache;
    Camera camera = new Camera();

    Matrix4f projection = new Matrix4f();
    Matrix4f model = new Matrix4f();
    float SCALE = 0.0025f;

    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(1, cores - 1));


    public void start(Window win) {

        init(win);
        run(win);

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
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,};

        VBO = GL15.glGenBuffers();
        VAO = GL30.glGenVertexArrays();

        GL30.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        int stride = 5 * Float.BYTES;

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);


        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);

        MemoryUtil.memFree(vertexBuffer);

        shader = new Shader(new Shader.ShaderSource(Shader.ShaderType.VERTEX, "/shaders/shader.vert"), new Shader.ShaderSource(Shader.ShaderType.FRAGMENT, "/shaders/shader.frag"));

        shader.use();
        shader.setInt("tex", 0);

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
        pageCache = new PageCache(pageManager, threadPool);
    }

    public void run(Window win) {
        while (!GLFW.glfwWindowShouldClose(win.window)) {
            input(win.window);

            // Every iteration to handle window resizes
            projection.identity().ortho(
                    -win.aspectRatio,
                    win.aspectRatio,
                    -1f, 1f,
                    -1f, 1f);

            draw();

            GLFW.glfwSwapBuffers(win.window);
            GLFW.glfwPollEvents();
        }
        pageCache.shutdown();
    }

    private void draw() {
        GL11.glClearColor(0.0f / 255.0f, 25.0f / 255.0f, 53 / 255.0f, 0.8f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shader.use();
        shader.setMat4("projection", projection);
        shader.setMat4("view", camera.getView());


        GL30.glBindVertexArray(VAO);

        for (RenderPage page : pageManager.getVisiblePages(camera)) {

            Integer texture = pageCache.get(page, camera.zoom);

            /* this is required because on first frame get() always returns null
             * because page has just been queued and takes time for background thread to finish
             */
            if (texture == null)
                continue;

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, texture);

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
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            camera.y += 0.05f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            camera.y -= 0.05f;
        }

        // zoom
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            camera.zoom += 0.01f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            camera.zoom -= 0.01f;
        }

        // clamp zoom
        camera.zoom = Math.max(0.2f, Math.min(3.0f, camera.zoom));
    }

    // a VERY temporary solution to load PDFs other than the sample pdf (in resources/pdf/test.pdf)
    private File choosePDFFile() {
        FileDialog dialog = new FileDialog((Frame) null, "Select a PDF File", FileDialog.LOAD);

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
