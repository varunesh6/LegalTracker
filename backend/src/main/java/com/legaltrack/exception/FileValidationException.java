package com.legaltrack.exception;

import org.springframework.http.HttpStatus;

public class FileValidationException extends ApiException {
    public FileValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
