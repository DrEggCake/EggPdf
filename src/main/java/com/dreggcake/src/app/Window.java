package com.dreggcake.src.app;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {

    public long window;
    public int width;
    public int height;
    public int posX;
    public int posY;
    public float aspectRatio;


    public Window(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspectRatio = (float) width / height;

        init();
    }

    private void init() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
        posX = (vidMode.width() - width) / 2;
        posY = (vidMode.height() - height) / 2;

        window = GLFW.glfwCreateWindow(width, height, "EggPdf", 0, 0);
        GLFW.glfwSetWindowPos(window, posX, posY);
    }
}
