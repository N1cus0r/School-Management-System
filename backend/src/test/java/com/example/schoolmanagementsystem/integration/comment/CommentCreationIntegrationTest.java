package com.example.schoolmanagementsystem.integration.comment;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.comment.CreateCommentRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CommentCreationIntegrationTest extends AbstractCommentIntegrationTest {
    private void createCommentForStudentAndExpectForbiddenStatus(
            String jwtToken,
            CreateCommentRequest createCommentRequest
    ) {
        client.post()
                .uri(COMMENTS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createCommentRequest), CreateCommentRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminAddCommentsForAnyStudent() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), studentId);

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseDTO.id());

        createCommentForStudentAndExpectOkStatus(jwtToken, createCommentRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        Long commentId = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate commentDatePublished = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::datePublished)
                .findFirst()
                .orElseThrow();

        CommentDTO expectedComment = new CommentDTO(
                commentId,
                createCommentRequest.text(),
                commentDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultComments).contains(expectedComment);
    }

    @Test
    void canTeacherAddCommentsForStudentItTeaches() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), studentId);

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseDTO.id());

        createCommentForStudentAndExpectOkStatus(teacherJwtToken, createCommentRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        Long commentId = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate commentDatePublished = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::datePublished)
                .findFirst()
                .orElseThrow();

        CommentDTO expectedComment = new CommentDTO(
                commentId,
                createCommentRequest.text(),
                commentDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultComments).contains(expectedComment);
    }

    @Test
    void canStudentsNotAddCommentsForStudent() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseId);

        createCommentForStudentAndExpectForbiddenStatus(studentJwtToken, createCommentRequest);
    }
}
