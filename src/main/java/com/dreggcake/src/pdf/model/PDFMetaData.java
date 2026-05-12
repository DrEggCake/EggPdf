package com.dreggcake.src.pdf.model;

public class PDFMetaData {

    private final String title;
    private final String author;
    private final int pageCount;

    public PDFMetaData(String title, String author, int pageCount) {
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String toString(){
        return "PDFMetadata{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", pageCount=" + pageCount +
                '}';
    }
}


