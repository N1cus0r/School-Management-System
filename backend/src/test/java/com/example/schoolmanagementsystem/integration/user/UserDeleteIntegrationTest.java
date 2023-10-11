package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.UserDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class UserDeleteIntegrationTest extends AbstractUserIntegrationTest {
    private void deleteUserAndExpectOkStatus(String jwtToken, Long userId) {
        client.delete()
                .uri(USERS_URI + "/%s".formatted(userId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void deleteUserAndExpectForbiddenStatus(String jwtToken, Long userId) {
        client.delete()
                .uri(USERS_URI + "/%s".formatted(userId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminDeleteTeacherAndStudent() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        deleteUserAndExpectOkStatus(jwtToken, studentId);

        deleteUserAndExpectOkStatus(jwtToken, teacherId);
    }

    @Test
    void canTeacherDeleteStudentAndNotTeacher() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest invalidTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, invalidTeacherRegistrationRequest);

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        Long invalidTeacherId = getUserByEmail(jwtToken, invalidTeacherRegistrationRequest.email()).id();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        deleteUserAndExpectOkStatus(teacherJwtToken, studentId);

        deleteUserAndExpectForbiddenStatus(teacherJwtToken, invalidTeacherId);
    }

    @Test
    void canStudentNotDeleteAnyUser() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest invalidStudentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, invalidStudentRegistrationRequest);

        Long invalidStudentId = getUserByEmail(jwtToken, invalidStudentRegistrationRequest.email()).id();

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        deleteUserAndExpectForbiddenStatus(studentJwtToken, invalidStudentId);

        deleteUserAndExpectForbiddenStatus(studentJwtToken, teacherId);
    }
}
