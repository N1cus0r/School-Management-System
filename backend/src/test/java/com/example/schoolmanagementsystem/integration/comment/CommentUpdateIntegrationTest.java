package com.example.schoolmanagementsystem.integration.comment;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.comment.CreateCommentRequest;
import com.example.schoolmanagementsystem.comment.UpdateCommentRequest;
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

public class CommentUpdateIntegrationTest extends AbstractCommentIntegrationTest {
    private void updateCommentAndExpectOkStatus(
            String jwtToken,
            Long commentId,
            UpdateCommentRequest updateCommentRequest
    ) {
        client.put()
                .uri(COMMENTS_URI + "/%s".formatted(commentId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateCommentRequest), UpdateCommentRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void updateCommentAndExpectForbiddenStatus(
            String jwtToken,
            Long commentId,
            UpdateCommentRequest updateCommentRequest
    ) {
        client.put()
                .uri(COMMENTS_URI + "/%s".formatted(commentId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateCommentRequest), UpdateCommentRequest.class)
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

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

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

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        updateCommentAndExpectOkStatus(jwtToken, commentId, updateCommentRequest);

        resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        CommentDTO expectedComment = new CommentDTO(
                commentId,
                updateCommentRequest.text(),
                commentDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultComments).contains(expectedComment);
    }

    @Test
    void canTeacherUpdateAnyCommentItCreated() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

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

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        updateCommentAndExpectOkStatus(teacherJwtToken, commentId, updateCommentRequest);

        resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        CommentDTO expectedComment = new CommentDTO(
                commentId,
                updateCommentRequest.text(),
                commentDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultComments).contains(expectedComment);
    }

    @Test
    void canStudentNotUpdateComment() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), studentId);

        CreateCommentRequest createCommentRequest = getCreateCommentRequest(studentId, courseDTO.id());

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

        UpdateCommentRequest updateCommentRequest = getUpdateCommentRequest();

        updateCommentAndExpectForbiddenStatus(studentJwtToken, commentId, updateCommentRequest);
    }
}
