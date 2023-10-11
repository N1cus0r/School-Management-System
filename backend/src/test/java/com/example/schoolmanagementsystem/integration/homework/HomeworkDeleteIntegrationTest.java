package com.example.schoolmanagementsystem.integration.homework;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.homework.CreateHomeworkRequest;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class HomeworkDeleteIntegrationTest extends AbstractHomeworkIntegrationTest{
    private void deleteHomeworkAndExpectOkStatus(
            String jwtToken,
            Long homeworkId
    ) {
        client.delete()
                .uri(HOMEWORKS_URI + "/%s".formatted(homeworkId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void deleteHomeworkAndExpectOkForbidden(
            String jwtToken,
            Long courseId,
            Long homeworkId
    ) {
        client.delete()
                .uri(COURSES_URI + "/%s/homeworks/%s".formatted(courseId, homeworkId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminDeleteAnyHomework() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        CreateHomeworkRequest createHomeworkRequest = getCreateHomeworkRequest(courseId);

        createHomeworkForCourseAndExpectOkStatus(jwtToken,  createHomeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        deleteHomeworkAndExpectOkStatus(jwtToken, homeworkId);
    }

    @Test
    void canTeacherDeleteAnyHomeworkItCreated() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        CreateHomeworkRequest createHomeworkRequest = getCreateHomeworkRequest(courseId);

        createHomeworkForCourseAndExpectOkStatus(jwtToken,  createHomeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        deleteHomeworkAndExpectOkStatus(teacherJwtToken, homeworkId);
    }

    @Test
    void canStudentNotDeleteHomework() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        CreateHomeworkRequest createHomeworkRequest = getCreateHomeworkRequest(courseId);

        createHomeworkForCourseAndExpectOkStatus(jwtToken,  createHomeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        deleteHomeworkAndExpectOkForbidden(studentJwtToken, courseId, homeworkId);
    }
}
