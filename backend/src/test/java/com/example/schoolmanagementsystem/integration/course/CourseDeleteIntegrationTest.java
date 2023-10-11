package com.example.schoolmanagementsystem.integration.course;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CourseDeleteIntegrationTest extends AbstractCourseIntegrationTest {
    @Autowired
    private CourseRepository courseRepository;

    private void deleteTeacherCourseAndExpectOkStatus(
            String jwtToken,
            Long courseId
    ) {

        client.delete()
                .uri(COURSES_URI + "/%s".formatted(courseId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void deleteTeacherCourseAndExpectForbiddenStatus(
            String jwtToken,
            Long courseId
    ) {

        client.delete()
                .uri(COURSES_URI + "/%s".formatted(courseId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }


    @Test
    void canAdminDeleteCourses() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        deleteTeacherCourseAndExpectOkStatus(jwtToken, courseId);
    }

    @Test
    void canTeacherDeleteItsCourses() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest anotherTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        Long anotherTeacherId = getUserByEmailAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest.email()).id();

        CreateCourseRequest createTeacherCreateCourseRequest = getCreateCourseRequest(teacherId);

        CreateCourseRequest createAnotherTeacherCreateCourseRequest = getCreateCourseRequest(anotherTeacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createTeacherCreateCourseRequest);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createAnotherTeacherCreateCourseRequest);

        Long teacherCourseId = getCourseByName(jwtToken, createTeacherCreateCourseRequest.name()).id();

        Long anotherTeacherCourseId = getCourseByName(jwtToken, createAnotherTeacherCreateCourseRequest.name()).id();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        deleteTeacherCourseAndExpectForbiddenStatus(teacherJwtToken, anotherTeacherCourseId);

        deleteTeacherCourseAndExpectOkStatus(teacherJwtToken, teacherCourseId);

        assertThat(courseRepository.existsById(teacherCourseId)).isFalse();
    }

    @Test
    void canStudentNotDeleteCourses() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

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

        deleteTeacherCourseAndExpectForbiddenStatus(studentJwtToken, courseId);
    }
}
