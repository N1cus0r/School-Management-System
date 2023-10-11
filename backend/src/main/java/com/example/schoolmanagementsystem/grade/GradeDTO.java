package com.example.schoolmanagementsystem.grade;

import java.time.LocalDate;

public record GradeDTO(
        Long id,
        Integer value,
        String text,
        LocalDate datePublished,
        String courseName,
        String courseTeacher
) {}
