package com.example.schoolmanagementsystem.grade;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateGradeRequest(
        @NotNull(message = "value must not be empty")
        @Min(value = 1, message = "value must be at least 1")
        @Max(value = 10, message = "value must be at most 10")
        Integer value,
        @NotEmpty(message = "text must not be empty")
        String text,
        @NotNull(message = "studentId name must not be empty")
        Long studentId,
        @NotNull(message = "courseId name must not be empty")
        Long courseId
) {}
