package com.example.schoolmanagementsystem.integration.comment;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.comment.CreateCommentRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CommentDeleteIntegrationTest extends AbstractCommentIntegrationTest {
    private void deleteCommentAndExpectOkStatus(
            String jwtToken,
            Long commentId
    ) {
        client.delete()
                .uri(COMMENTS_URI + "/%s".formatted(commentId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void deleteCommentAndExpectForbiddenStatus(
            String jwtToken,
            Long commentId
    ) {
        client.delete()
                .uri(COMMENTS_URI + "/%s".formatted(commentId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminUpdateAnyComment() throws IOException {
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

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseId);

        createCommentForStudentAndExpectOkStatus(jwtToken, createCommentRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        Long commentId = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::id)
                .findFirst()
                .orElseThrow();

        deleteCommentAndExpectOkStatus(jwtToken, commentId);
    }

    @Test
    void canTeacherDeleteAnyCommentItCreated() throws IOException {
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

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseId);

        createCommentForStudentAndExpectOkStatus(jwtToken, createCommentRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        Long commentId = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::id)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        deleteCommentAndExpectOkStatus(teacherJwtToken, commentId);
    }

    @Test
    void canStudentNotDeleteComment() throws IOException {
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

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseId);

        createCommentForStudentAndExpectOkStatus(jwtToken, createCommentRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        Long commentId = resultComments.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(CommentDTO::id)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        deleteCommentAndExpectForbiddenStatus(studentJwtToken, commentId);
    }
}
