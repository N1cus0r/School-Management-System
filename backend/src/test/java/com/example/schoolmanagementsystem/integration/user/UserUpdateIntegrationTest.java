package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class UserUpdateIntegrationTest extends AbstractUserIntegrationTest {
    private UpdateUserRequest getUserUpdateRequest() {
        return new UpdateUserRequest(
                FAKER.internet().safeEmailAddress(),
                FAKER.name().fullName(),
                Gender.MALE,
                null,
                LocalDate.now()
        );
    }

    private UpdateUserRequest getAdminUpdateRequest() {
        return getUserUpdateRequest();
    }

    private UpdateUserRequest getStudentUpdateRequest() {
        return getUserUpdateRequest();
    }

    private UpdateUserRequest getTeacherUpdateRequest() {
        return getUserUpdateRequest();
    }

    private void updateUserAndExpectOkStatus(String jwtToken, Long userId, UpdateUserRequest request) {
        client.put()
                .uri(USERS_URI + "/%s".formatted(userId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void updateUserAndExpectForbiddenStatus(String jwtToken, Long userId, UpdateUserRequest request) {
        client.put()
                .uri(USERS_URI + "/%s".formatted(userId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private boolean isUserUpdatedSuccessfully(UserDTO resultUser, UpdateUserRequest updateRequest) {
        return updateRequest.fullName().equals(resultUser.fullName()) &&
                updateRequest.email().equals(updateRequest.email()) &&
                updateRequest.gender().equals(updateRequest.gender()) &&
                updateRequest.dateOfBirth().equals(updateRequest.dateOfBirth());
    }

    @Test
    void canAdminTeacherAndStudent() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        UpdateUserRequest teacherUpdateRequest = getTeacherUpdateRequest();

        UpdateUserRequest studentUpdateRequest = getStudentUpdateRequest();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        updateUserAndExpectOkStatus(jwtToken, teacherId, teacherUpdateRequest);

        updateUserAndExpectOkStatus(jwtToken, studentId, studentUpdateRequest);

        UserDTO updatedTeacher = getUserByEmailAndExpectOkStatus(jwtToken, teacherUpdateRequest.email());

        UserDTO updatedStudent = getUserByEmailAndExpectOkStatus(jwtToken, studentUpdateRequest.email());

        assertThat(isUserUpdatedSuccessfully(updatedTeacher, teacherUpdateRequest)).isTrue();

        assertThat(isUserUpdatedSuccessfully(updatedStudent, studentUpdateRequest)).isTrue();
    }

    @Test
    void canTeacherUpdateStudentAndNotItselfAndNotAdmin() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        UpdateUserRequest adminUpdateRequest = getAdminUpdateRequest();

        UpdateUserRequest teacherUpdateRequest = getTeacherUpdateRequest();

        UpdateUserRequest studentUpdateRequest = getStudentUpdateRequest();

        Long adminId = getAdminUser().getId();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        updateUserAndExpectForbiddenStatus(teacherJwtToken, adminId, adminUpdateRequest);

        updateUserAndExpectForbiddenStatus(teacherJwtToken, teacherId, teacherUpdateRequest);

        updateUserAndExpectOkStatus(teacherJwtToken, studentId, studentUpdateRequest);

        UserDTO updatedStudent = getUserByEmailAndExpectOkStatus(jwtToken, studentUpdateRequest.email());

        assertThat(isUserUpdatedSuccessfully(updatedStudent, studentUpdateRequest)).isTrue();
    }

    @Test
    void canStudentNotUpdateAnyone() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        UpdateUserRequest adminUpdateRequest = getAdminUpdateRequest();

        UpdateUserRequest teacherUpdateRequest = getTeacherUpdateRequest();

        UpdateUserRequest studentUpdateRequest = getStudentUpdateRequest();

        Long adminId = getAdminUser().getId();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        updateUserAndExpectForbiddenStatus(studentJwtToken, adminId, adminUpdateRequest);

        updateUserAndExpectForbiddenStatus(studentJwtToken, teacherId, teacherUpdateRequest);

        updateUserAndExpectForbiddenStatus(studentJwtToken, studentId, studentUpdateRequest);
    }
}
