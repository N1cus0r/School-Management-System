package com.example.schoolmanagementsystem.integration.homework;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.homework.CreateHomeworkRequest;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class HomeworkCreationIntegrationTest extends AbstractHomeworkIntegrationTest {
    private void createHomeworkForCourseAndExpectForbiddenStatus(
            String jwtToken,
            CreateHomeworkRequest homeworkRequest
    ) {
        client.post()
                .uri(HOMEWORKS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(homeworkRequest), CreateHomeworkRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminAddHomeworkForAnyCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseDTO.id());

        createHomeworkForCourseAndExpectOkStatus(jwtToken,  homeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate homeworkDatePublished = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::datePublished)
                .findFirst()
                .orElseThrow();

        HomeworkDTO expectedHomework = new HomeworkDTO(
                homeworkId,
                homeworkRequest.text(),
                homeworkDatePublished,
                homeworkRequest.dueDate(),
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultHomeworks).contains(expectedHomework);
    }

    @Test
    void canTeacherAddHomeworkForCourseItTeaches() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseDTO.id());

        createHomeworkForCourseAndExpectOkStatus(teacherJwtToken,  homeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate homeworkDatePublished = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::datePublished)
                .findFirst()
                .orElseThrow();

        HomeworkDTO expectedHomework = new HomeworkDTO(
                homeworkId,
                homeworkRequest.text(),
                homeworkDatePublished,
                homeworkRequest.dueDate(),
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultHomeworks).contains(expectedHomework);
    }

    @Test
    void canStudentsNotAddHomeworksForCourse() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest courseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, courseRequest);

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        createHomeworkForCourseAndExpectForbiddenStatus(studentJwtToken, getCreateHomeworkRequest(teacherId));
    }
}
