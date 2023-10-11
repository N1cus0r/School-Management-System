package com.example.schoolmanagementsystem.grade;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GradeDTOMapper implements Function<Grade, GradeDTO> {
    @Override
    public GradeDTO apply(Grade grade) {
        return new GradeDTO(
                grade.getId(),
                grade.getValue(),
                grade.getText(),
                grade.getDatePublished(),
                grade.getCourse().getName(),
                grade.getCourse().getTeacher().getFullName()
        );
    }
}
