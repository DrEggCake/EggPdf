package com.dreggcake.src.pdf.core;

import com.dreggcake.src.exceptions.PdfLoadException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public class PDFLoader {

    public static PDFDocument loadDocument(String path) {

        try {
          PDDocument document = Loader.loadPDF(
                  new RandomAccessReadBufferedFile(path)
          );

          return new PDFDocument(document);

        } catch (IOException e) {
            throw new PdfLoadException("Failed to load pdf: " + path, e);
        }


    }
}
