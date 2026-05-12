package com.dreggcake.src.renderer;

import org.joml.Matrix4f;

public class Camera {

    float y = 0.0f;
    float zoom = 1.0f;

    public Matrix4f getView(){

        return new Matrix4f()
                .translate(0, y, 0)
                .scale(zoom);
    }
}