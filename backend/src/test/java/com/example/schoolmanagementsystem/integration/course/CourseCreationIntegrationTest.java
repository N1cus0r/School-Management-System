package com.example.schoolmanagementsystem.integration.course;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CourseCreationIntegrationTest extends AbstractCourseIntegrationTest {
    private void createCourseForTeacherAndExpectForbiddenStatus(
            String jwtToken,
            CreateCourseRequest createCourseRequest
    ) {
        client.post()
                .uri(COURSES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createCourseRequest), CreateCourseRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    void canAdminAddCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest courseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, courseRequest);

        List<CourseDTO> resultCourses = getCoursesSearchByNameAndExpectOkStatus(jwtToken, courseRequest.name());

        Long courseId = getCourseIdByNameFromResultList(courseRequest.name(), resultCourses);

        CourseDTO expectedCourse =
                new CourseDTO(courseId, courseRequest.name(), teacherRegistrationRequest.fullName());

        assertThat(resultCourses).contains(expectedCourse);
    }

    @Test
    void canTeacherCreateCoursesForItself() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest anotherTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(teacherJwtToken,  createCourseRequest);

        List<CourseDTO> resultCourses = getCoursesSearchByNameAndExpectOkStatus(jwtToken, createCourseRequest.name());

        Long courseId = getCourseIdByNameFromResultList(createCourseRequest.name(), resultCourses);

        CourseDTO expectedCourse =
                new CourseDTO(courseId, createCourseRequest.name(), teacherRegistrationRequest.fullName());

        assertThat(resultCourses).contains(expectedCourse);
    }

    @Test
    void canStudentsNotCreateCourses() throws IOException {
        String jwtToken = getAdminJwtToken();

        Long teacherId = FAKER.number().randomNumber();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        createCourseForTeacherAndExpectForbiddenStatus(studentJwtToken, getCreateCourseRequest(teacherId));
    }
}
