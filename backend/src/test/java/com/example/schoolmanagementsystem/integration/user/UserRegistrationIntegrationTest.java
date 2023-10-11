package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.user.UserDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public class UserRegistrationIntegrationTest extends AbstractUserIntegrationTest {

    private void registerUserAndExpectForbiddenStatus(
            String jwtToken,
            UserRegistrationRequest request
    ) {
        client.post()
                .uri(USERS_URI + "/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private boolean isUserCreatedSuccessfully(UserDTO resultUser, UserRegistrationRequest userRegistrationRequest) {
        return userRegistrationRequest.fullName().equals(resultUser.fullName()) &&
                userRegistrationRequest.gender().equals(resultUser.gender()) &&
                userRegistrationRequest.dateOfBirth().equals(resultUser.dateOfBirth());
    }

    @Test
    void canAdminRegisterTeacherAndStudentAndNotAdmin() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest adminRegistrationRequest =
                getAdminRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectForbiddenStatus(jwtToken, adminRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        UserDTO student = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email());

        UserDTO teacher = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email());

        assertThat(isUserCreatedSuccessfully(student, studentRegistrationRequest));

        assertThat(isUserCreatedSuccessfully(teacher, studentRegistrationRequest));
    }

    @Test
    void canTeacherRegisterStudentAndNotTeacherAndAdmin() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest adminRegistrationRequest =
                getAdminRegistrationRequest();

        UserRegistrationRequest invalidTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        registerUserAndExpectForbiddenStatus(teacherJwtToken, adminRegistrationRequest);

        registerUserAndExpectForbiddenStatus(teacherJwtToken, invalidTeacherRegistrationRequest);

        registerUserAndExpectOkStatus(teacherJwtToken, studentRegistrationRequest);

        UserDTO student = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email());

        assertThat(isUserCreatedSuccessfully(student, studentRegistrationRequest));
    }

    @Test
    void canStudentNotRegisterUsers() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest invalidStudentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest adminRegistrationRequest =
                getAdminRegistrationRequest();


        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        registerUserAndExpectForbiddenStatus(studentJwtToken, invalidStudentRegistrationRequest);

        registerUserAndExpectForbiddenStatus(studentJwtToken, teacherRegistrationRequest);

        registerUserAndExpectForbiddenStatus(studentJwtToken, adminRegistrationRequest);
    }
}
