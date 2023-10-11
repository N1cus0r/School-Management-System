package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.homework.Homework;
import com.example.schoolmanagementsystem.homework.HomeworkRepository;
import com.example.schoolmanagementsystem.user.Gender;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

//    @Bean
//    CommandLineRunner runner(
//            PasswordEncoder passwordEncoder,
//            UserRepository userRepository,
//            CourseRepository courseRepository
//    ) {
//        return args -> {
//            User teacher = createUserByRole(Role.TEACHER, "john@example.com", "password", userRepository, passwordEncoder);
//
//            User student = createUserByRole(Role.STUDENT, userRepository, passwordEncoder);
//
//            Course course = createCourse(teacher, courseRepository);
//
//            addCoursesToStudent(userRepository, student, List.of(course));
//        };
//    }

    private static User createUserByRole(Role role, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        Faker FAKER = new Faker();

        return userRepository.save(
                User.builder()
                        .role(role)
                        .email(FAKER.internet().safeEmailAddress())
                        .password(passwordEncoder.encode(FAKER.internet().password()))
                        .fullName(FAKER.name().firstName())
                        .gender(Gender.MALE)
                        .build()
        );
    }

    private static User createUserByRole(
            Role role,
            String email,
            String password,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        Faker FAKER = new Faker();

        return userRepository.save(
                User.builder()
                        .role(role)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .fullName(FAKER.name().firstName())
                        .gender(Gender.MALE)
                        .build()
        );
    }

    private static Course createCourse(User teacher, CourseRepository courseRepository) {
        Faker FAKER = new Faker();

        return courseRepository.save(
                Course.builder()
                        .name(FAKER.lorem().word())
                        .teacher(teacher)
                        .build()
        );
    }

    private static void createCourse(String courseName, User teacher, CourseRepository courseRepository) {
        Faker FAKER = new Faker();

        courseRepository.save(
                Course.builder()
                        .name(courseName + FAKER.lorem().word())
                        .teacher(teacher)
                        .build()
        );
    }
    private void addCoursesToStudent(
            UserRepository userRepository,
            User student,
            List<Course> courses
    ) {
        student.getCourses().addAll(courses);
        userRepository.save(student);
    }

    private static void createHomework(HomeworkRepository homeworkRepository, Course course) {
        Faker FAKER = new Faker();

        homeworkRepository.save(
                Homework.builder()
                        .text(FAKER.lorem().sentence())
                        .dueDate(LocalDate.of(2023, 10, 2))
                        .course(course)
                        .build()
        );
    }
}
