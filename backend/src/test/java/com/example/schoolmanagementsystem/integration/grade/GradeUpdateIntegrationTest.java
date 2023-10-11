package com.example.schoolmanagementsystem.integration.grade;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.comment.UpdateCommentRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.grade.CreateGradeRequest;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.grade.UpdateGradeRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class GradeUpdateIntegrationTest extends AbstractGradeIntegrationTest {
    private void updateGradeAndExpectOkStatus(
            String jwtToken,
            Long gradeId,
            UpdateGradeRequest updateGradeRequest
    ) {
        client.put()
                .uri(GRADES_URI + "/%s".formatted(gradeId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateGradeRequest), UpdateGradeRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void updateGradeAndExpectForbiddenStatus(
            String jwtToken,
            Long gradeId,
            UpdateGradeRequest updateGradeRequest
    ) {
        client.put()
                .uri(GRADES_URI + "/%s".formatted(gradeId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateGradeRequest), UpdateCommentRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminUpdateAnyGrade() throws IOException {
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

        CreateGradeRequest createGradeRequest = getCreateGradeRequest(studentId, courseDTO.id());

        createGradeForStudentAndExpectOkStatus(jwtToken, createGradeRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        Long gradeId = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate gradeDatePublished = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::datePublished)
                .findFirst()
                .orElseThrow();

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        updateGradeAndExpectOkStatus(jwtToken, gradeId, updateGradeRequest);

        resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        GradeDTO expectedGrade = new GradeDTO(
                gradeId,
                updateGradeRequest.value(),
                updateGradeRequest.text(),
                gradeDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultGrades).contains(expectedGrade);
    }

    @Test
    void canTeacherUpdateAnyGradeItCreated() throws IOException {
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

        CreateGradeRequest createGradeRequest = getCreateGradeRequest(studentId, courseDTO.id());

        createGradeForStudentAndExpectOkStatus(jwtToken, createGradeRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        Long gradeId = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate gradeDatePublished = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::datePublished)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        updateGradeAndExpectOkStatus(teacherJwtToken, gradeId, updateGradeRequest);

        resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        GradeDTO expectedGrade = new GradeDTO(
                gradeId,
                updateGradeRequest.value(),
                updateGradeRequest.text(),
                gradeDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultGrades).contains(expectedGrade);
    }

    @Test
    void canStudentNotUpdateGrade() throws IOException {
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

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

        CreateGradeRequest createGradeRequest = getCreateGradeRequest(studentId, courseId);

        createGradeForStudentAndExpectOkStatus(jwtToken, createGradeRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        Long gradeId = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::id)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        updateGradeAndExpectForbiddenStatus(studentJwtToken, gradeId, updateGradeRequest);
    }
}
