package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.user.User;

import java.util.HashSet;

public abstract class AbstractCourseRelatedServiceTest extends AbstractServiceTest {
    public Course createCourseForTeacher(User teacher) {
        return Course.builder()
                .name(FAKER.lorem().word() + " " + FAKER.lorem().word())
                .teacher(teacher)
                .students(new HashSet<>())
                .build();
    }
}
