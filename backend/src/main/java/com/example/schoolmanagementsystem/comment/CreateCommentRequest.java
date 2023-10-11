package com.example.schoolmanagementsystem.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotEmpty(message = "text must not be empty")
        String text,
        @NotNull(message = "studentId name must not be empty")
        Long studentId,
        @NotNull(message = "courseId name must not be empty")
        Long courseId
) {}
