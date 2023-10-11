package com.example.schoolmanagementsystem.exception;

public class NotEnoughAuthorityException extends RuntimeException {
    public NotEnoughAuthorityException(String message) {
        super(message);
    }
}
