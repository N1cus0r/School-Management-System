package com.example.schoolmanagementsystem.exception;

public class RequestValidationError extends RuntimeException {
    public RequestValidationError(String message) {
        super(message);
    }
}
