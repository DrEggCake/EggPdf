package com.dreggcake.src.pdf.core;

import com.dreggcake.src.exceptions.PdfLoadException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.io.InputStream;

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
    public static PDFDocument loadDocumentFromResource(
            String resourcePath
    ) {

        try {

            InputStream stream =
                    PDFLoader.class.getResourceAsStream(
                            resourcePath
                    );

            if (stream == null) {
                throw new PdfLoadException(
                        "Resource not found: " + resourcePath,
                        null
                );
            }

            PDDocument document =
                    Loader.loadPDF(
                            new RandomAccessReadBuffer(
                                    stream.readAllBytes()
                            )
                    );

            return new PDFDocument(document);

        } catch (IOException e) {

            throw new PdfLoadException(
                    "Failed to load pdf from resources: "
                            + resourcePath,
                    e
            );
        }
    }
}
