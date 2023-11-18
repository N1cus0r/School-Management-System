package com.example.schoolmanagementsystem.integration.grade;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.grade.CreateGradeRequest;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.user.UserDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class GradeCreationIntegrationTest extends AbstractGradeIntegrationTest {
    private void createGradeForStudentAndExpectForbiddenStatus(
            String jwtToken,
            CreateGradeRequest createGradeRequest
    ) {
        client.post()
                .uri(GRADES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createGradeRequest), CreateGradeRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminAddGradesForAnyStudent() throws IOException {
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

        UserDTO student = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email());

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), student.id());

        CreateGradeRequest createGradeRequest = getCreateGradeRequest(student.id(), courseDTO.id());

        createGradeForStudentAndExpectOkStatus(jwtToken, createGradeRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        Long commentId = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate gradeDatePublished = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::datePublished)
                .findFirst()
                .orElseThrow();

        GradeDTO expectedGrade = new GradeDTO(
                commentId,
                createGradeRequest.value(),
                createGradeRequest.text(),
                gradeDatePublished,
                courseDTO.name(),
                courseDTO.teacherName(),
                student.fullName()
        );

        assertThat(resultGrades).contains(expectedGrade);
    }

    @Test
    void canTeacherAddGradesForStudentItTeaches() throws IOException {
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

        UserDTO student = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email());

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), student.id());

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        CreateGradeRequest createGradeRequest = getCreateGradeRequest(student.id(), courseDTO.id());

        createGradeForStudentAndExpectOkStatus(teacherJwtToken, createGradeRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        Long commentId = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate gradeDatePublished = resultGrades.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(GradeDTO::datePublished)
                .findFirst()
                .orElseThrow();

        GradeDTO expectedGrade = new GradeDTO(
                commentId,
                createGradeRequest.value(),
                createGradeRequest.text(),
                gradeDatePublished,
                courseDTO.name(),
                courseDTO.teacherName(),
                student.fullName()
        );

        assertThat(resultGrades).contains(expectedGrade);
    }

    @Test
    void canStudentsNotAddGradesForStudent() throws IOException {
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

        CreateGradeRequest createGradeRequest = getCreateGradeRequest(studentId, courseId);

        createGradeForStudentAndExpectForbiddenStatus(studentJwtToken, createGradeRequest);
    }
}
