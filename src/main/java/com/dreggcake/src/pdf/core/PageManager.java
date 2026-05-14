package com.dreggcake.src.pdf.core;

import com.dreggcake.src.pdf.model.PDFPage;
import com.dreggcake.src.renderer.Camera;
import com.dreggcake.src.renderer.RenderPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.util.ArrayList;
import java.util.List;

public class PageManager {

    private final PDFDocument document;
    private final float SCALE = 0.0025f;
    private final float PAGE_SPACING = 0.05f;


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

            float scaledHeight = renderPage.getPage().getHeight() * SCALE;

            currentY -= scaledHeight + PAGE_SPACING; // scale down the page, then add a constant amount of gap between two pages

            pages.add(renderPage);
        }
    }

    public List<RenderPage> getVisiblePages(Camera camera) {
        List<RenderPage> visiblePages = new ArrayList<>();

        float viewTop = (camera.y + 1.0f) / camera.zoom;
        float viewBottom = (camera.y - 1.0f) / camera.zoom;

        for (RenderPage page : pages) {

            float pageTop = page.y + page.getPage().getHeight();
            float pageBottom = page.y - page.getPage().getHeight();

            boolean isVisible = !(pageBottom > viewTop || pageTop < viewBottom);

            if (isVisible) {
                visiblePages.add(page);
            }
        }

        return visiblePages;
    }

    public List<RenderPage> getPages() {
        return pages;
    }

    public PDFDocument getDocument() {
        return document;
    }

}
