package com.example.schoolmanagementsystem.dataGenerator;

import com.example.schoolmanagementsystem.attendance.*;
import com.example.schoolmanagementsystem.comment.Comment;
import com.example.schoolmanagementsystem.comment.CommentRepository;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.grade.Grade;
import com.example.schoolmanagementsystem.grade.GradeRepository;
import com.example.schoolmanagementsystem.homework.Homework;
import com.example.schoolmanagementsystem.homework.HomeworkRepository;
import com.example.schoolmanagementsystem.user.Gender;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
@Profile("preview")
@RequiredArgsConstructor
public class DataGeneratorService {
    private final Faker FAKER = new Faker();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final HomeworkRepository homeworkRepository;
    private final AttendanceRepository attendanceRepository;
    private final CommentRepository commentRepository;

    private <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass) {
        Random random = new Random();
        T[] values = enumClass.getEnumConstants();
        int index = random.nextInt(values.length);
        return values[index];
    }

    public User createUserWithCredentialsWithRole(String email, String password, Role role) {
        return userRepository.save(
                User.builder()
                        .role(role)
                        .email(email)
                        .fullName(FAKER.name().fullName())
                        .password(passwordEncoder.encode(password))
                        .gender(getRandomEnumValue(Gender.class))
                        .build()
        );
    }

    public void createTeachers(int numberOfTeachers){
        for (int i = 0; i < numberOfTeachers; i++) {
            createUserWithRole(Role.TEACHER);
        }
    }

    public void createStudents(int numberOfStudents){
        for (int i = 0; i < numberOfStudents; i++) {
            createUserWithRole(Role.STUDENT);
        }
    }

    public void createUserWithRole(Role role) {
        createUserWithCredentialsWithRole(FAKER.internet().safeEmailAddress(), FAKER.lorem().word(), role);
    }

    public Course createCourseWithTeacher(User teacher) {
        return courseRepository.save(
                Course.builder()
                        .name(FAKER.lorem().sentence(3))
                        .teacher(teacher)
                        .build()
        );
    }

    public Set<Course> createCoursesWithTeacher(User teacher, int numberOfCourses) {
        Set<Course> courses = new HashSet<>();

        for (int i = 0; i < numberOfCourses; i++) {
            courses.add(createCourseWithTeacher(teacher));
        }

        return courses;
    }

    public void createHomeworks(Course course, int numberOfHomeworks) {
        for (int i = 0; i < numberOfHomeworks; i++) {
            createHomework(course);
        }
    }

    public void createHomework(Course course) {
        homeworkRepository.save(
                Homework.builder()
                        .text(FAKER.lorem().sentence(50))
                        .dueDate(LocalDate.now().plusDays(FAKER.number().numberBetween(0, 3)))
                        .course(course)
                        .build()
        );
    }

    public void createGrades(Course course, User student, int numberOfGrades) {
        for (int i = 0; i < numberOfGrades; i++) {
            createGrade(course, student);
        }
    }

    public void createGrade(Course course, User student) {
        gradeRepository.save(
                Grade.builder()
                        .value(FAKER.number().numberBetween(4, 10))
                        .text(FAKER.lorem().sentence())
                        .course(course)
                        .student(student)
                        .build()
        );
    }

    public void createAttendances(Course course, User student, int numberOfAttendances) {
        for (int i = 0; i < numberOfAttendances; i++) {
            createAttendance(course, student);
        }
    }

    public void createAttendance(Course course, User student) {
        attendanceRepository.save(
                Attendance.builder()
                        .type(getRandomEnumValue(AttendanceType.class))
                        .period(getRandomEnumValue(AttendancePeriod.class))
                        .student(student)
                        .course(course)
                        .build()
        );
    }

    public void createComments(Course course, User student, int numberOfComments) {
        for (int i = 0; i < numberOfComments; i++) {
            createComment(course, student);
        }
    }

    public void createComment(Course course, User student) {
        commentRepository.save(
                Comment.builder()
                        .text(FAKER.lorem().sentence(100))
                        .course(course)
                        .student(student)
                        .build()
        );
    }

    public void addCoursesToStudent(Set<Course> courses, User student){
        student.getCourses().addAll(courses);
        userRepository.save(student);
    }
}
