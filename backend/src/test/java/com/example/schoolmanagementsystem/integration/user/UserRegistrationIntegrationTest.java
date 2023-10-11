package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.user.Role;
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

        List<UserDTO> students = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.STUDENT);

        Long studentId = getUserIdByEmailFromResultList(
                studentRegistrationRequest.email(),
                students
        );

        LocalDate studentRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        studentRegistrationRequest.email(),
                        students
                );

        UserDTO expectedStudent = getExpectedUserFromRegistrationRequest(
                studentId,
                studentRegistrationDate,
                studentRegistrationRequest
        );

        List<UserDTO> teachers = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.TEACHER);

        Long teacherId = getUserIdByEmailFromResultList(
                teacherRegistrationRequest.email(),
                teachers
        );

        LocalDate teacherRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        teacherRegistrationRequest.email(),
                        teachers
                );

        UserDTO expectedTeacher =
                getExpectedUserFromRegistrationRequest(
                        teacherId,
                        teacherRegistrationDate,
                        teacherRegistrationRequest
                );

        assertThat(students).contains(expectedStudent);

        assertThat(teachers).contains(expectedTeacher);
    }

    @Test
    void canTeacherRegisterStudentAndNotTeacherAndAdmin() throws IOException {
        String adminJwtToken = getAdminJwtToken();

        UserRegistrationRequest adminRegistrationRequest =
                getAdminRegistrationRequest();

        UserRegistrationRequest invalidTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(adminJwtToken, teacherRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        registerUserAndExpectForbiddenStatus(teacherJwtToken, adminRegistrationRequest);

        registerUserAndExpectForbiddenStatus(teacherJwtToken, invalidTeacherRegistrationRequest);

        registerUserAndExpectOkStatus(teacherJwtToken, studentRegistrationRequest);

        List<UserDTO> students = getAllUsersByRoleAndExpectOkStatus(teacherJwtToken, Role.STUDENT);

        Long studentId = getUserIdByEmailFromResultList(
                studentRegistrationRequest.email(),
                students
        );

        LocalDate studentRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        studentRegistrationRequest.email(),
                        students
                );

        UserDTO expectedStudent = getExpectedUserFromRegistrationRequest(
                studentId,
                studentRegistrationDate,
                studentRegistrationRequest
        );

        assertThat(students).contains(expectedStudent);
    }

    @Test
    void canStudentNotRegisterUsers() throws IOException {
        String adminJwtToken = getAdminJwtToken();

        UserRegistrationRequest invalidStudentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest adminRegistrationRequest =
                getAdminRegistrationRequest();


        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(adminJwtToken, studentRegistrationRequest);

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
