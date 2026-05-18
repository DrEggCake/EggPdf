package com.dreggcake.src.renderer;

import com.dreggcake.src.pdf.model.PDFPage;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public class RenderPage {

    private final PDFPage page;

    public CompletableFuture<BufferedImage> future;

    public float x;
    public float y;

    public int texture = -1;
    public boolean loaded = false;

    public RenderPage(PDFPage page) {
        this.page = page;
    }


    public PDFPage getPage() {
        return page;
    }
}
