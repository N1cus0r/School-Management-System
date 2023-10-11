package com.example.schoolmanagementsystem.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationError(
        String path,
        Map<String, String> fields,
        Integer statusCode,
        LocalDateTime localDateTime
)  {}
