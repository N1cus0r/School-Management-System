package com.example.schoolmanagementsystem.homework;

import com.example.schoolmanagementsystem.AbstractCourseDependentEntityRepositoryTest;
import com.example.schoolmanagementsystem.AbstractRepositoryTest;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HomeworkRepositoryTest extends AbstractCourseDependentEntityRepositoryTest {
    @Autowired
    private HomeworkRepository homeworkRepository;


    private Homework createHomeworkForCourse(Course course) {
        return homeworkRepository.save(
                Homework.builder()
                        .text(FAKER.lorem().sentence())
                        .dueDate(LocalDate.now())
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

        Homework homework = createHomeworkForCourse(course);

        Pageable page = PageRequest.of(0, 1);

        Page<Homework> resultHomeworkPage =
                homeworkRepository.findByCourseStudentsId(student.getId(), page);


        assertThat(resultHomeworkPage.getContent())
                .contains(homework);
    }

    @Test
    void findByCourseTeacherId() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        Homework homework = createHomeworkForCourse(course);

        Pageable page = PageRequest.of(0, 1);

        Page<Homework> resultHomeworkPage = homeworkRepository.findByCourseTeacherId(teacher.getId(), page);

        assertThat(resultHomeworkPage.getContent())
                .contains(homework);
    }

    @Test
    void findByCourseId() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        Homework homework = createHomeworkForCourse(course);

        Pageable page = PageRequest.of(0, 1);

        Page<Homework> resultHomeworkPage = homeworkRepository.findByCourseId(course.getId(), page);

        assertThat(resultHomeworkPage.getContent())
                .contains(homework);
    }
}