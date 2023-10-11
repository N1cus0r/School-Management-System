package com.example.schoolmanagementsystem.integration.course;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.course.UpdateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CourseUpdateIntegrationTest extends CourseCreationIntegrationTest {
    private void updateTeacherCourseAndExpectOkStatus(
            String jwtToken,
            Long courseId,
            UpdateCourseRequest updateCourseRequest
    ) {
        client.put()
                .uri(COURSES_URI + "/%s".formatted(courseId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateCourseRequest), CreateCourseRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void updateTeacherCourseAndExpectForbiddenStatus(
            String jwtToken,
            Long courseId,
            UpdateCourseRequest updateCourseRequest
    ) {
        client.put()
                .uri(COURSES_URI + "/%s".formatted(courseId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateCourseRequest), CreateCourseRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminUpdateCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        UpdateCourseRequest updateCourseRequest = getUpdateCourseRequest();

        updateTeacherCourseAndExpectOkStatus(jwtToken, courseId, updateCourseRequest);

        CourseDTO updatedCourse = getCourseByName(jwtToken, updateCourseRequest.name());

        assertThat(updatedCourse.name())
                .isEqualTo(updateCourseRequest.name());
    }

    @Test
    void canTeacherUpdateItsCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest anotherTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        Long anotherTeacherId = getUserByEmail(jwtToken, anotherTeacherRegistrationRequest.email()).id();

        CreateCourseRequest createTeacherCreateCourseRequest = getCreateCourseRequest(teacherId);

        CreateCourseRequest createAnotherTeacherCreateCourseRequest = getCreateCourseRequest(anotherTeacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createTeacherCreateCourseRequest);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createAnotherTeacherCreateCourseRequest);

        UpdateCourseRequest updateTeacherCourseRequest = getUpdateCourseRequest();

        Long teacherCourseId = getCourseByName(jwtToken, createTeacherCreateCourseRequest.name()).id();

        Long anotherTeacherCourseId = getCourseByName(jwtToken, createAnotherTeacherCreateCourseRequest.name()).id();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        updateTeacherCourseAndExpectForbiddenStatus(teacherJwtToken, anotherTeacherCourseId, getUpdateCourseRequest());

        updateTeacherCourseAndExpectOkStatus(teacherJwtToken, teacherCourseId, updateTeacherCourseRequest);

        CourseDTO updatedCourse = getCourseByName(jwtToken, updateTeacherCourseRequest.name());

        assertThat(updatedCourse.name())
                .isEqualTo(updateTeacherCourseRequest.name());
    }

    @Test
    void canStudentNotUpdateCourses() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        updateTeacherCourseAndExpectForbiddenStatus(studentJwtToken, courseId,  getUpdateCourseRequest());
    }
}
