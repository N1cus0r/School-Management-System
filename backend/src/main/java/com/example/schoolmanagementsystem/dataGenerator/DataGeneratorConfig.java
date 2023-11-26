package com.example.schoolmanagementsystem.dataGenerator;

import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataGeneratorConfig {
    private final DataGeneratorService service;

    @Bean
    @Profile("preview")
    public CommandLineRunner runner() {
        return args -> {
            User teacher = service.createUserWithCredentialsWithRole("teacher@example.com", "password", Role.TEACHER);
            User student = service.createUserWithCredentialsWithRole("student@example.com", "password", Role.STUDENT);

            Set<Course> courses = service.createCoursesWithTeacher(teacher, 10);

            service.addCoursesToStudent(courses, student);

            for (Course course : courses) {
                service.createHomeworks(course, 50);
                service.createGrades(course, student, 50);
                service.createAttendances(course, student, 50);
                service.createComments(course, student, 50);
            }

            service.createCoursesWithTeacher(teacher, 50);

            service.createTeachers(50);

            service.createStudents(50);
        };
    }
}
