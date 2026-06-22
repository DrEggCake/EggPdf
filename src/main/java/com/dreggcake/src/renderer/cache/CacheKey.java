package com.dreggcake.src.renderer.cache;

public record CacheKey(int pageIndex, float scaleBucket) {

    static float getScaleBucket(float zoom) {
        if (zoom <= 1.0f) return 1.0f;
        if (zoom <= 2.0f) return 2.0f;
        if (zoom <= 4.0f) return 4.0f;
        return 8.0f;
    }
}
