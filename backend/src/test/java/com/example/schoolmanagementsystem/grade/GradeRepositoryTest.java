package com.example.schoolmanagementsystem.grade;

import com.example.schoolmanagementsystem.AbstractCourseDependentEntityRepositoryTest;
import com.example.schoolmanagementsystem.comment.Comment;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GradeRepositoryTest extends AbstractCourseDependentEntityRepositoryTest {
    @Autowired
    private GradeRepository gradeRepository;
    private Grade createGradeForStudent(
            User student,
            Course course
    ) {
        return gradeRepository.save(
                Grade.builder()
                        .value(FAKER.number().numberBetween(1, 10))
                        .text(FAKER.lorem().sentence())
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

        Grade grade = createGradeForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Grade> resultGradePage =
                gradeRepository.findByCourseStudentsId(student.getId(), page);

        assertThat(resultGradePage.getContent())
                .contains(grade);
    }

    @Test
    void findByCourseTeacherId() {
        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Set<User> students = new HashSet<>();

        students.add(student);

        Course course = createCourseForTeacherWithStudents(teacher, students);

        Grade grade = createGradeForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Grade> resultGradePage = gradeRepository.findByCourseTeacherId(teacher.getId(), page);

        assertThat(resultGradePage.getContent())
                .contains(grade);
    }

    @Test
    void findByCourseId() {
        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Set<User> students = new HashSet<>();

        students.add(student);

        Course course = createCourseForTeacherWithStudents(teacher, students);

        Grade grade = createGradeForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Grade> resultGradePage = gradeRepository.findByCourseId(course.getId(), page);

        assertThat(resultGradePage.getContent())
                .contains(grade);
    }
}