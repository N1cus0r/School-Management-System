package com.example.schoolmanagementsystem.homework;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class HomeworkDTOMapper implements Function<Homework, HomeworkDTO> {

    @Override
    public HomeworkDTO apply(Homework homework) {
        return new HomeworkDTO(
                homework.getId(),
                homework.getText(),
                homework.getDatePublished(),
                homework.getDueDate(),
                homework.getCourse().getName(),
                homework.getCourse().getTeacher().getFullName()
        );
    }
}
