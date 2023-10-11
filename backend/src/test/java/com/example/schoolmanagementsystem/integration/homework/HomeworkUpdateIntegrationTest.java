package com.example.schoolmanagementsystem.integration.homework;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.homework.CreateHomeworkRequest;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.homework.UpdateHomeworkRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class HomeworkUpdateIntegrationTest extends AbstractHomeworkIntegrationTest {
    private void updateHomeworkAndExpectOkStatus(
            String jwtToken,
            Long homeworkId,
            UpdateHomeworkRequest updateHomeworkRequest
    ) {
        client.put()
                .uri(HOMEWORKS_URI + "/%s".formatted(homeworkId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateHomeworkRequest), UpdateHomeworkRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void updateHomeworkAndExpectForbiddenStatus(
            String jwtToken,
            Long homeworkId,
            UpdateHomeworkRequest updateHomeworkRequest
    ) {
        client.put()
                .uri(HOMEWORKS_URI + "/%s".formatted(homeworkId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateHomeworkRequest), UpdateHomeworkRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminUpdateAnyHomework() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        CreateHomeworkRequest createHomeworkRequest = getCreateHomeworkRequest(courseDTO.id());

        createHomeworkForCourseAndExpectOkStatus(jwtToken,  createHomeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        updateHomeworkAndExpectOkStatus(jwtToken,  homeworkId, updateHomeworkRequest);

        resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        LocalDate homeworkDatePublished = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::datePublished)
                .findFirst()
                .orElseThrow();

        HomeworkDTO updatedHomework = new HomeworkDTO(
                homeworkId,
                updateHomeworkRequest.text(),
                homeworkDatePublished,
                updateHomeworkRequest.dueDate(),
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultHomeworks).contains(updatedHomework);
    }

    @Test
    void canTeacherUpdateAnyHomeworkItCreated() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        CreateHomeworkRequest createHomeworkRequest = getCreateHomeworkRequest(courseDTO.id());

        createHomeworkForCourseAndExpectOkStatus(jwtToken,  createHomeworkRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        Long homeworkId = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::id)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        updateHomeworkAndExpectOkStatus(teacherJwtToken, homeworkId, updateHomeworkRequest);

        resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        LocalDate homeworkDatePublished = resultHomeworks.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(HomeworkDTO::datePublished)
                .findFirst()
                .orElseThrow();

        HomeworkDTO updatedHomework = new HomeworkDTO(
                homeworkId,
                updateHomeworkRequest.text(),
                homeworkDatePublished,
                updateHomeworkRequest.dueDate(),
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultHomeworks).contains(updatedHomework);
    }

    @Test
    void canStudentNotUpdateHomework() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

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

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        updateHomeworkAndExpectForbiddenStatus(studentJwtToken, homeworkId, updateHomeworkRequest);
    }
}
