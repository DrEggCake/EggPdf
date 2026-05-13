package com.dreggcake.src.renderer;

import com.dreggcake.src.pdf.model.PDFPage;

public class RenderPage {

    private final PDFPage page;

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
