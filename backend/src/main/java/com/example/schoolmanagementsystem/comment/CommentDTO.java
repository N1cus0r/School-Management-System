package com.example.schoolmanagementsystem.comment;

import java.time.LocalDate;

public record CommentDTO(
        Long id,
        String text,
        LocalDate datePublished,
        String courseName,
        String courseTeacher,
        String studentName
) {}
