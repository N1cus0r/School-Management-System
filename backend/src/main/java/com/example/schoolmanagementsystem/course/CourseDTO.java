package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.user.User;

public record CourseDTO(
        Long id,
        String name,
        String teacherName
) {
}
