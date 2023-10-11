package com.example.schoolmanagementsystem.integration.course;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AddingStudentToCourseIntegrationTest extends AbstractCourseIntegrationTest {
    private void addStudentToCourseAndExpectForbiddenStatus(
            String jwtToken,
            Long courseId,
            Long studentId
            ) {
        client.put()
                .uri(COURSES_URI + "/%s/add-student/%s".formatted(courseId, studentId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminAddStudentToCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);
    }

    @Test
    void canTeacherAddStudentToCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        addStudentToCourseAndExpectOkStatus(teacherJwtToken, courseId, studentId);
    }
    @Test
    void canStudentNotAddStudentToCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        addStudentToCourseAndExpectForbiddenStatus(studentJwtToken, courseId, studentId);
    }
}
