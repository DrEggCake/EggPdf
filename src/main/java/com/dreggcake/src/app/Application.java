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

        // Force the X11 backend so OpenGL is created through GLX. This lets the
        // PRIME offload env vars (__NV_PRIME_RENDER_OFFLOAD=1,
        // __GLX_VENDOR_LIBRARY_NAME=nvidia, as used by prime-run) select the
        // dedicated GPU. Under a Wayland session this runs via XWayland.
        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_X11);

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        // Explicitly route context creation through GLX (native API) so the
        // GLX PRIME offload variables above actually take effect.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, GLFW.GLFW_NATIVE_CONTEXT_API);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        win = new Window(800, 600);

        GLFW.glfwMakeContextCurrent(win.window);

        GL.createCapabilities();
        GLFW.glfwSwapInterval(1);

        // On Wayland (e.g. Hyprland) an unfocused window stops receiving frame
        // callbacks, which makes glfwSwapBuffers block forever. If we never call
        // glfwPollEvents, the compositor marks the app as "not responding" and
        // offers to kill it. Disabling vsync while unfocused lets the loop keep
        // polling events without blocking on a swap that never returns.
        GLFW.glfwSetWindowFocusCallback(win.window, (window, focused) -> {
            GLFW.glfwSwapInterval(focused ? 1 : 0);
        });

        GL11.glViewport(0, 0, win.width, win.height);

        GLFW.glfwSetFramebufferSizeCallback(win.window, (window, w, h) -> {
            GL11.glViewport(0, 0, w, h);

            win.width = w;
            win.height = h;
            win.aspectRatio = (float) w /h;
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
