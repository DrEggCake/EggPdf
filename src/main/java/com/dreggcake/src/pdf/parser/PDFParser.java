package com.dreggcake.src.pdf.parser;

import com.dreggcake.src.pdf.model.PDFMetaData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class PDFParser {

    public static PDFMetaData parseMetaData(PDDocument document) {
        PDDocumentInformation info = document.getDocumentInformation();

        return new PDFMetaData(
                info.getTitle(),
                info.getAuthor(),
                document.getNumberOfPages()
        );
    }
}
