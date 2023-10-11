package com.example.schoolmanagementsystem.homework;

import java.time.LocalDate;

public record HomeworkDTO(
        Long id,
        String text,
        LocalDate datePublished,
        LocalDate dueDate,
        String courseName,
        String courseTeacher
) {}
