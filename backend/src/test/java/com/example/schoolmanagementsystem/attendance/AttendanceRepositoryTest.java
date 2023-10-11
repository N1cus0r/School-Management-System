package com.example.schoolmanagementsystem.attendance;

import com.example.schoolmanagementsystem.AbstractCourseDependentEntityRepositoryTest;
import com.example.schoolmanagementsystem.AbstractRepositoryTest;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.homework.Homework;
import com.example.schoolmanagementsystem.homework.HomeworkRepository;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceRepositoryTest extends AbstractCourseDependentEntityRepositoryTest {
    @Autowired
    private AttendanceRepository attendanceRepository;

    private Attendance createAttendanceForStudent(
            User student,
            Course course
    ) {
        return attendanceRepository.save(
                Attendance.builder()
                        .type(AttendanceType.ABSENT)
                        .period(AttendancePeriod.LESSON_3)
                        .student(student)
                        .course(course)
                        .build()
        );
    }

    @Test
    void findByCourseStudentsId() {
        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Set<User> students = new HashSet<>();

        students.add(student);

        Course course = createCourseForTeacherWithStudents(teacher, students);

        Attendance attendance = createAttendanceForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Attendance> resultAttendancePage =
                attendanceRepository.findByCourseStudentsId(student.getId(), page);

        assertThat(resultAttendancePage.getContent())
                .contains(attendance);
    }

    @Test
    void findByCourseTeacherId() {
        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Set<User> students = new HashSet<>();

        students.add(student);

        Course course = createCourseForTeacherWithStudents(teacher, students);

        Attendance attendance = createAttendanceForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Attendance> resultAttendancePage = attendanceRepository.findByCourseTeacherId(teacher.getId(), page);

        assertThat(resultAttendancePage.getContent())
                .contains(attendance);
    }
}