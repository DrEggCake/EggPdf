package com.dreggcake.src.exceptions;

public class PdfLoadException extends RuntimeException {
    public PdfLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
