package com.example.schoolmanagementsystem.homework;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateHomeworkRequest(
    @NotNull(message = "text must not be empty")
    String text,
    @NotNull(message = "dueDate name must not be empty")
    LocalDate dueDate,
    @NotNull(message = "courseId name must not be empty")
    Long courseId
) {}
