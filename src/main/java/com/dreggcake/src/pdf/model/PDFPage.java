package com.dreggcake.src.pdf.model;

public class PDFPage {

    final int index;

    final float width;
    final float height;

    public PDFPage(int index, float width, float height) {
        this.index = index;
        this.width = width;
        this.height = height;
    }

    public int getIndex() {
        return index;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
