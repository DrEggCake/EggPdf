package com.dreggcake.src.pdf.core;

import com.dreggcake.src.pdf.model.PDFPage;
import com.dreggcake.src.renderer.Camera;
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

    public List<RenderPage> getVisiblePages(Camera camera) {
        List<RenderPage> visiblePages = new ArrayList<>();

        float viewTop = camera.y + camera.zoom;
        float viewBottom = camera.y - camera.zoom;

        for (RenderPage page : pages) {

            // 0.7 just the temp hard page size for now
            float pageTop = page.y + 0.7f;
            float pageBottom = page.y - 0.7f;

            boolean isVisible = !(pageBottom > viewTop || pageTop < viewBottom);

            if(isVisible){
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
