package com.dreggcake.src.renderer;

import org.joml.Matrix4f;

public class Camera {

    public float y = 0.0f;
    public float zoom = 1.0f;

    public Matrix4f getView(){

        return new Matrix4f()
                .scale(zoom)
                .translate(0, -y, 0);
    }
}