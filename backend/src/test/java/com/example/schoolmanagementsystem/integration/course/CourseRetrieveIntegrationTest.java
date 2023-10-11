package com.example.schoolmanagementsystem.integration.course;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CourseRetrieveIntegrationTest extends AbstractCourseIntegrationTest {

    private void getCoursesSearchByNameAndExpectForbiddenStatus(String jwtToken, String search) {
        client.get()
                .uri(COURSES_URI + "?nameSearch=" + search)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }


    @Test
    void canAdminSearchCoursesByName() throws IOException {
        String jwtToken = getAdminJwtToken();

        String prefix = getRandomString();

        int oldNumberOfCourses = getCoursesSearchByNameAndExpectOkStatus(jwtToken, prefix).size();

        int numberOfCourses = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        for (int i = 0; i < numberOfCourses; i++) {
            createCourseForTeacherAndExpectOkStatus(jwtToken, getCreateCourseRequest(prefix, teacherId));
        }

        List<CourseDTO> resultCourses = getCoursesSearchByNameAndExpectOkStatus(jwtToken, prefix);

        assertThat(resultCourses.size())
                .isEqualTo(oldNumberOfCourses + numberOfCourses);
    }

    @Test
    void canTeacherSearchItsCoursesByName() throws IOException {
        String jwtToken = getAdminJwtToken();

        String prefix = getRandomString();

        int numberOfCoursesBelongingToTeacher = FAKER.number().numberBetween(2, 3);

        int numberOfCoursesNotBelongingToTeacher = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest anotherTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        Long anotherTeacherId = getUserByEmailAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest.email()).id();

        for (int i = 0; i < numberOfCoursesBelongingToTeacher; i++) {
            createCourseForTeacherAndExpectOkStatus(jwtToken, getCreateCourseRequest(prefix, teacherId));
        }

        for (int i = 0; i < numberOfCoursesNotBelongingToTeacher; i++) {
            createCourseForTeacherAndExpectOkStatus(jwtToken, getCreateCourseRequest(prefix, anotherTeacherId));
        }

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        List<CourseDTO> resultCourses = getCoursesSearchByNameAndExpectOkStatus(teacherJwtToken, prefix);

        assertThat(resultCourses.size())
                .isEqualTo(numberOfCoursesBelongingToTeacher);
    }

    @Test
    void canStudentNotSearchCoursesByName() throws IOException {
        String jwtToken = getAdminJwtToken();

        String prefix = getRandomString();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        getCoursesSearchByNameAndExpectForbiddenStatus(studentJwtToken, prefix);
    }
}
