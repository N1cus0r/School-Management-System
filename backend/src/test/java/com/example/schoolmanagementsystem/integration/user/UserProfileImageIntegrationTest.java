package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.shaded.com.google.common.io.Files;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class UserProfileImageIntegrationTest extends AbstractUserIntegrationTest {
    private void uploadUserProfileImageAndExpectOkStatus(String jwtToken, Long userId, Resource image) {
        MultipartBodyBuilder multipartBodyBuilder =
                new MultipartBodyBuilder();

        multipartBodyBuilder.part("file", image);

        client.put()
                .uri(USERS_URI + "/%s/profile-image".formatted(userId))
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void uploadUserProfileImageAndExpectForbiddenStatus(String jwtToken, Long userId, Resource image) {
        MultipartBodyBuilder multipartBodyBuilder =
                new MultipartBodyBuilder();

        multipartBodyBuilder.part("file", image);

        client.put()
                .uri(USERS_URI + "/%s/profile-image".formatted(userId))
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private byte[] getUserProfileImageAndExpectOkStatus(String jwtToken, Long userId) {
        return client.get()
                .uri(USERS_URI + "/%s/profile-image".formatted(userId))
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();
    }

    private void getUserProfileImageAndExpectForbiddenStatus(String jwtToken, Long userId) {
        client.get()
                .uri(USERS_URI + "/%s/profile-image".formatted(userId))
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminUploadStudentAndTeacherUserImage() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        Resource image =
                new ClassPathResource(
                        "%s.jpg"
                                .formatted(studentRegistrationRequest.gender().name().toLowerCase())
                );

        uploadUserProfileImageAndExpectOkStatus(jwtToken, studentId, image);

        uploadUserProfileImageAndExpectOkStatus(jwtToken, teacherId, image);

        byte[] studentImage = getUserProfileImageAndExpectOkStatus(jwtToken, studentId);

        byte[] teacherImage = getUserProfileImageAndExpectOkStatus(jwtToken, teacherId);

        byte[] imageBytes = Files.toByteArray(image.getFile());

        assertThat(imageBytes).isEqualTo(studentImage);

        assertThat(imageBytes).isEqualTo(teacherImage);
    }

    @Test
    void canTeacherUploadStudentUserImage() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        Resource image =
                new ClassPathResource(
                        "%s.jpg"
                                .formatted(studentRegistrationRequest.gender().name().toLowerCase())
                );

        uploadUserProfileImageAndExpectOkStatus(teacherJwtToken, studentId, image);

        byte[] studentImage = getUserProfileImageAndExpectOkStatus(jwtToken, studentId);

        byte[] imageBytes = Files.toByteArray(image.getFile());

        assertThat(imageBytes).isEqualTo(studentImage);
    }

    @Test
    void canStudentNotUploadAndGetAnyUserImage() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        UserRegistrationRequest anothetStudentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, anothetStudentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        Long anotherStudentId = getUserByEmailAndExpectOkStatus(jwtToken, anothetStudentRegistrationRequest.email()).id();

        Resource image =
                new ClassPathResource(
                        "%s.jpg"
                                .formatted(anothetStudentRegistrationRequest.gender().name().toLowerCase())
                );

        uploadUserProfileImageAndExpectForbiddenStatus(studentJwtToken, anotherStudentId, image);

        getUserProfileImageAndExpectForbiddenStatus(studentJwtToken, anotherStudentId);
    }
}
