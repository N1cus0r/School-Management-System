package com.example.schoolmanagementsystem.course;

import jakarta.validation.constraints.NotNull;

public record CreateCourseRequest(
        @NotNull(message = "name must not be empty")
        String name,
        @NotNull(message = "teacherId must not be empty")
        Long teacherId
) {
}
