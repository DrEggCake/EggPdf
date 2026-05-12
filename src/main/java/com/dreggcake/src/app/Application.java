package com.dreggcake.src.app;

import com.dreggcake.src.renderer.Renderer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Application {
    Window win;
    Renderer renderer;


    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        win = new Window(800, 600);

        GLFW.glfwMakeContextCurrent(win.window);

        GL.createCapabilities();
        GLFW.glfwSwapInterval(1);

        GL11.glViewport(0, 0, win.width, win.height);

        GLFW.glfwSetFramebufferSizeCallback(win.window, (window, w, h) -> {
            GL11.glViewport(0, 0, w, h);
        });

        renderer = new Renderer();


    }

    private void loop() {
        renderer.start(win);
    }

    private void cleanup() {
        GLFW.glfwTerminate();

    }
}
