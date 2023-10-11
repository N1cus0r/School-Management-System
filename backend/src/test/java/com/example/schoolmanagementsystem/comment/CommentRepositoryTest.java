package com.example.schoolmanagementsystem.comment;

import com.example.schoolmanagementsystem.AbstractCourseDependentEntityRepositoryTest;
import com.example.schoolmanagementsystem.attendance.Attendance;
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
import static org.junit.jupiter.api.Assertions.*;

class CommentRepositoryTest extends AbstractCourseDependentEntityRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    
    private Comment createCommentForStudent(
            User student,
            Course course
    ) {
        return commentRepository.save(
                Comment.builder()
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

        Comment comment = createCommentForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Comment> resultCommentPage =
                commentRepository.findByCourseStudentsId(student.getId(), page);

        assertThat(resultCommentPage.getContent())
                .contains(comment);
    }

    @Test
    void findByCourseTeacherId() {
        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Set<User> students = new HashSet<>();

        students.add(student);

        Course course = createCourseForTeacherWithStudents(teacher, students);

        Comment comment = createCommentForStudent(student, course);

        Pageable page = PageRequest.of(0, 1);

        Page<Comment> resultCommentPage = commentRepository.findByCourseTeacherId(teacher.getId(), page);

        assertThat(resultCommentPage.getContent())
                .contains(comment);
    }
}