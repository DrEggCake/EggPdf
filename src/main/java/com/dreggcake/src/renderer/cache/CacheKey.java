package com.dreggcake.src.renderer.cache;

/**
 * Renders each page at a resolution chosen dynamically from the current zoom,
 * rather than a fixed set of DPI buckets. Because PDFs are vector content at
 * 72 pt/inch, we rasterize at a scale that always covers the on-screen pixel
 * density at the current zoom (so the raster is never up-scaled blurry), while
 * never dropping below a 1.0x baseline so text stays crisp when zoomed out.
 *
 * The scale is quantized to 0.25 steps so that small zoom deltas share a cache
 * bucket instead of re-rendering on every scroll tick.
 */
public record CacheKey(int pageIndex, float scaleBucket) {

    /**
     * Returns the PDF page render scale for the given display zoom. The value
     * is a pure render scale (1.0 == 72 dpi) and is intended to be passed
     * straight to the rasteriser.
     */
    static float getScaleBucket(float zoom) {
        float target = Math.max(zoom, 1.0f);
        target = Math.min(target, 8.0f);
        return Math.max(1.0f, Math.round(target * 4.0f) / 4.0f);
    }
}
