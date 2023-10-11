package com.example.schoolmanagementsystem.course;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CourseDTOMapper implements Function<Course, CourseDTO> {
    @Override
    public CourseDTO apply(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getTeacher().getFullName()
        );
    }
}
