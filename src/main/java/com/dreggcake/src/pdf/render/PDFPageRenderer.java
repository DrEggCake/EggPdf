package com.dreggcake.src.pdf.render;

import com.dreggcake.src.exceptions.PdfRenderException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFPageRenderer {

    public static BufferedImage renderPage(PDDocument document, int pageIndex, float scale) {

        try {
            PDFRenderer renderer = new PDFRenderer(document);

            return renderer.renderImage(pageIndex, scale);
        } catch (IOException e) {
            throw new PdfRenderException("Failed to render page", e);
        }

    }

}
