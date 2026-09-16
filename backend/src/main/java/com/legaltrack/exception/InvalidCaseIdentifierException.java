package com.legaltrack.exception;

import org.springframework.http.HttpStatus;

public class InvalidCaseIdentifierException extends ApiException {
    public InvalidCaseIdentifierException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
