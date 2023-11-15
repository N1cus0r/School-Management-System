package com.example.schoolmanagementsystem.exception;

public class CourseNameTakenException extends RuntimeException {
    public CourseNameTakenException(String message) {
        super(message);
    }
}
