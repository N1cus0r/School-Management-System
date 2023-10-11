package com.example.schoolmanagementsystem.comment;

import com.example.schoolmanagementsystem.AbstractCourseRelatedServiceTest;
import com.example.schoolmanagementsystem.attendance.Attendance;
import com.example.schoolmanagementsystem.attendance.UpdateAttendanceRequest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = CommentService.class)
public class CommentServiceTest extends AbstractCourseRelatedServiceTest {
    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CommentDTOMapper commentDTOMapper;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationUtil authenticationUtil;

    @MockBean
    private UpdateUtil updateUtil;

    @Autowired
    private CommentService commentService;

    private Comment createCommentForStudent(
            User student,
            Course course
    ){
        return Comment.builder()
                .text(FAKER.lorem().sentence())
                .student(student)
                .course(course)
                .build();
    }

    private CreateCommentRequest getCreateCommentRequest(
            Long studentId,
            Long courseId
    ) {
        return new CreateCommentRequest(
                FAKER.lorem().sentence(),
                studentId,
                courseId
        );
    }

    private UpdateCommentRequest getUpdateCommentRequest() {
        return new UpdateCommentRequest(FAKER.lorem().sentence());
    }

    @Test
    void getAllCommentsByAdmin() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.ADMIN);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Comment> comments = List.of(
                createCommentForStudent(student, course),
                createCommentForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(commentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(comments, pageable, comments.size()));

        List<CommentDTO> resultComments =
                commentService.getAllComments(pageCount, pageSize);

        assertThat(resultComments)
                .containsExactlyElementsOf(comments.stream().map(commentDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllCommentsByTeacherItself() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long teacherId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        teacher.setId(teacherId);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Comment> comments = List.of(
                createCommentForStudent(student, course),
                createCommentForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(commentRepository.findByCourseTeacherId(teacherId, pageable))
                .thenReturn(new PageImpl<>(comments, pageable, comments.size()));

        List<CommentDTO> resultComments =
                commentService.getAllComments(pageCount, pageSize);

        assertThat(resultComments)
                .containsExactlyElementsOf(comments.stream().map(commentDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllCommentsByStudent() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long studentId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        student.setId(studentId);

        Course course = createCourseForTeacher(teacher);

        List<Comment> comments = List.of(
                createCommentForStudent(student, course),
                createCommentForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(student);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(commentRepository.findByCourseStudentsId(studentId, pageable))
                .thenReturn(new PageImpl<>(comments, pageable, comments.size()));

        List<CommentDTO> resultAttendances =
                commentService.getAllComments(pageCount, pageSize);

        assertThat(resultAttendances)
                .containsExactlyElementsOf(comments.stream().map(commentDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void addCommentForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(true);

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        ArgumentCaptor<Comment> commentArgumentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        commentService.addCommentForUser(createCommentRequest);

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment expectedComment =
                Comment.builder()
                        .text(createCommentRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(commentArgumentCaptor.getValue())
                .isEqualTo(expectedComment);
    }

    @Test
    void addCommentForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(true);

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        ArgumentCaptor<Comment> commentArgumentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        commentService.addCommentForUser(createCommentRequest);

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment expectedComment =
                Comment.builder()
                        .text(createCommentRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(commentArgumentCaptor.getValue())
                .isEqualTo(expectedComment);
    }

    @Test
    void addCommentForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(true);

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        assertThatThrownBy(() -> commentService.addCommentForUser(createCommentRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to add comment for this course");
    }

    @Test
    void addCommentForUnexistingCourse() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        assertThatThrownBy(() -> commentService.addCommentForUser(createCommentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void addCommentForUnexistingStudent() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        assertThatThrownBy(() -> commentService.addCommentForUser(createCommentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(studentId));
    }

    @Test
    void addCommentForStudentNotBelongingToCourse() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(false);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        assertThatThrownBy(() -> commentService.addCommentForUser(createCommentRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("Student does not belong to course");
    }

    @Test
    void addCommentWithInsufficientAuthority() {
        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        CreateCommentRequest createCommentRequest =
                getCreateCommentRequest(studentId, courseId);

        assertThatThrownBy(() -> commentService.addCommentForUser(createCommentRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void updateCommentForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        ArgumentCaptor<Comment> commentArgumentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        commentService.updateCommentForUser(
                commentId,
                updateCommentRequest
        );

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment expectedComment =
                Comment.builder()
                        .text(updateCommentRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(commentArgumentCaptor.getValue())
                .isEqualTo(expectedComment);
    }

    @Test
    void updateCommentForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        ArgumentCaptor<Comment> commentArgumentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        commentService.updateCommentForUser(
                commentId,
                updateCommentRequest
        );

        verify(commentRepository).save(commentArgumentCaptor.capture());

        Comment expectedComment =
                Comment.builder()
                        .text(updateCommentRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(commentArgumentCaptor.getValue())
                .isEqualTo(expectedComment);
    }

    @Test
    void updateCommentForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        assertThatThrownBy(() -> commentService
                .updateCommentForUser(commentId, updateCommentRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this comment");
    }

    @Test
    void updateCommentForUserWithoutChanges() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(updateUtil.isFieldNullOrWithoutChange(any(), any()))
                .thenReturn(true);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        assertThatThrownBy(() -> commentService
                .updateCommentForUser(commentId, updateCommentRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("No data changes found");
    }

    @Test
    void updateUnexistingComment() {
        Long commentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        assertThatThrownBy(() -> commentService
                .updateCommentForUser(commentId, updateCommentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment with id [%s] does not exist".formatted(commentId));
    }

    @Test
    void updateCommentForUserWithInsufficientAuthority() {
        Long commentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        assertThatThrownBy(() -> commentService
                .updateCommentForUser(commentId, updateCommentRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void deleteCommentForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        ArgumentCaptor<Comment> commentArgumentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        commentService.deleteCommentForUser(commentId);

        verify(commentRepository).delete(commentArgumentCaptor.capture());

        assertThat(commentArgumentCaptor.getValue())
                .isEqualTo(comment);
    }

    @Test
    void deleteCommentForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        ArgumentCaptor<Comment> commentArgumentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        commentService.deleteCommentForUser(commentId);

        verify(commentRepository).delete(commentArgumentCaptor.capture());

        assertThat(commentArgumentCaptor.getValue())
                .isEqualTo(comment);
    }

    @Test
    void deleteCommentForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long commentId = FAKER.number().randomNumber();

        Comment comment = createCommentForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> commentService.deleteCommentForUser(commentId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to delete this comment");
    }

    @Test
    void deleteUnexistingComment() {
        Long commentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        assertThatThrownBy(() -> commentService.deleteCommentForUser(commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment with id [%s] does not exist".formatted(commentId));
    }

    @Test
    void deleteCommentForUserWithInsufficientAuthority() {
        Long commentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> commentService.deleteCommentForUser(commentId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }
}
