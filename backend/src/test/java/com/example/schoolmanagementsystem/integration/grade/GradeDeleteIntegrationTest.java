package com.example.schoolmanagementsystem.integration.grade;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.grade.CreateGradeRequest;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class GradeDeleteIntegrationTest extends AbstractGradeIntegrationTest {
    private void deleteGradeAndExpectOkStatus(
            String jwtToken,
            Long gradeId
    ) {
        client.delete()
                .uri(GRADES_URI + "/%s".formatted(gradeId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void deleteGradeAndExpectForbiddenStatus(
            String jwtToken,
            Long gradeId
    ) {
        client.delete()
                .uri(GRADES_URI + "/%s".formatted(gradeId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminDeleteAnyGrade() throws IOException {
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

        deleteGradeAndExpectOkStatus(jwtToken, gradeId);
    }

    @Test
    void canTeacherDeleteAnyGradeItCreated() throws IOException {
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

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        deleteGradeAndExpectOkStatus(teacherJwtToken, gradeId);
    }

    @Test
    void canStudentNotDeleteGrade() throws IOException {
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

        deleteGradeAndExpectForbiddenStatus(studentJwtToken, gradeId);
    }
}
