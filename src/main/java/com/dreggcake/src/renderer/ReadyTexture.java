package com.dreggcake.src.renderer;

import java.nio.ByteBuffer;

public record ReadyTexture(ByteBuffer buffer, int width, int height) {
}
