package com.example.schoolmanagementsystem.homework;

import java.time.LocalDate;

public record UpdateHomeworkRequest(
        String text,
        LocalDate dueDate
) {}
