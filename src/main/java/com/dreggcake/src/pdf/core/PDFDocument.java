package com.dreggcake.src.pdf.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFDocument implements AutoCloseable {

    private final PDDocument document;
    private final PDFRenderer renderer;

    public PDFDocument(PDDocument document) {
        this.document = document;
        this.renderer = new PDFRenderer(document);
    }

    public int getPageCount() {
        return document.getNumberOfPages();
    }

    public BufferedImage renderPage(
            int pageIndex,
            float zoom
    ) {

        try {

            return renderer.renderImage(
                    pageIndex,
                    zoom
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to render page: " + pageIndex,
                    e
            );
        }
    }

    public PDDocument unwrap() {
        return document;
    }

    @Override
    public void close() {

        try {
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to close PDF document",
                    e
            );
        }
    }
}