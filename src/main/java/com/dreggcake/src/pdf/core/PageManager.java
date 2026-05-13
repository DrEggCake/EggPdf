package com.dreggcake.src.pdf.core;

import com.dreggcake.src.pdf.model.PDFPage;
import com.dreggcake.src.renderer.RenderPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.util.ArrayList;
import java.util.List;

public class PageManager {

    private final PDFDocument document;


    List<RenderPage> pages = new ArrayList<>();

    public PageManager(PDFDocument document) {
        this.document = document;

        buildPages();
    }

    private void buildPages() {

        float currentY = 0;

        for (int i = 0; i < document.getPageCount(); i++) {
            PDRectangle box = document.unwrap()
                    .getPage(i)
                    .getMediaBox();

            PDFPage pdfPage = new PDFPage(i, box.getWidth(), box.getHeight());
            RenderPage renderPage = new RenderPage(pdfPage);

            renderPage.x = 0;
            renderPage.y = currentY;

            currentY -= 1.8f;

            pages.add(renderPage);
        }
    }

    public List<RenderPage> getPages() {
        return pages;
    }

    public PDFDocument getDocument(){
        return document;
    }

}
