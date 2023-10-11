package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.user.User;

import java.util.Set;

public abstract class AbstractCourseDependentEntityRepositoryTest extends AbstractRepositoryTest {
    public Course createCourseForTeacherWithStudents(User teacher, Set<User> students) {
        Course course = courseRepository.save(
                Course.builder()
                        .name(FAKER.lorem().word() + " " + FAKER.lorem().word())
                        .teacher(teacher)
                        .students(students)
                        .build()
        );

        students.forEach(student -> {
            student.getCourses().add(course);
            userRepository.save(student);
        });

        return course;
    }
}
