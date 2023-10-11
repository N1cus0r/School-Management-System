package com.example.schoolmanagementsystem.integration.attendance;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.attendance.CreateAttendanceRequest;
import com.example.schoolmanagementsystem.attendance.UpdateAttendanceRequest;
import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AttendanceUpdateIntegrationTest extends AbstractAttendanceIntegrationTest {
    private void updateAttendanceAndExpectOkStatus(
            String jwtToken,
            Long attendanceId,
            UpdateAttendanceRequest updateAttendanceRequest
    ) {
        client.put()
                .uri(ATTENDANCES_URI + "/%s".formatted(attendanceId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateAttendanceRequest), UpdateAttendanceRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void updateAttendanceAndExpectForbiddenStatus(
            String jwtToken,
            Long attendanceId,
            UpdateAttendanceRequest updateAttendanceRequest
    ) {
        client.put()
                .uri(ATTENDANCES_URI + "/%s".formatted(attendanceId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(updateAttendanceRequest), UpdateAttendanceRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminUpdateAnyAttendance() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), studentId);

        CreateAttendanceRequest createAttendanceRequest = getCreateAttendanceRequest(studentId, courseDTO.id());

        createAttendanceForStudentAndExpectOkStatus(jwtToken, createAttendanceRequest);

        List<AttendanceDTO> resultAttendances = getAllAttendancesAndExpectOkStatus(jwtToken);

        Long attendanceId = resultAttendances.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(AttendanceDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate attendanceDatePublished = resultAttendances.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(AttendanceDTO::datePublished)
                .findFirst()
                .orElseThrow();

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateAttendanceRequest();

        updateAttendanceAndExpectOkStatus(jwtToken, attendanceId, updateAttendanceRequest);

        resultAttendances = getAllAttendancesAndExpectOkStatus(jwtToken);

        AttendanceDTO updatedAttendance = new AttendanceDTO(
                attendanceId,
                updateAttendanceRequest.type(),
                updateAttendanceRequest.period(),
                attendanceDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultAttendances).contains(updatedAttendance);
    }

    @Test
    void canTeacherUpdateAnyAttendanceItCreated() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        CourseDTO courseDTO = getCourseByName(jwtToken, createCourseRequest.name());

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseDTO.id(), studentId);

        CreateAttendanceRequest createAttendanceRequest = getCreateAttendanceRequest(studentId, courseDTO.id());

        createAttendanceForStudentAndExpectOkStatus(jwtToken, createAttendanceRequest);

        List<AttendanceDTO> resultAttendances = getAllAttendancesAndExpectOkStatus(jwtToken);

        Long attendanceId = resultAttendances.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(AttendanceDTO::id)
                .findFirst()
                .orElseThrow();

        LocalDate attendanceDatePublished = resultAttendances.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(AttendanceDTO::datePublished)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateAttendanceRequest();

        updateAttendanceAndExpectOkStatus(teacherJwtToken, attendanceId, updateAttendanceRequest);

        resultAttendances = getAllAttendancesAndExpectOkStatus(jwtToken);

        AttendanceDTO updatedAttendance = new AttendanceDTO(
                attendanceId,
                updateAttendanceRequest.type(),
                updateAttendanceRequest.period(),
                attendanceDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultAttendances).contains(updatedAttendance);
    }

    @Test
    void canStudentNotUpdateAttendance() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken, createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

        CreateAttendanceRequest createAttendanceRequest = getCreateAttendanceRequest(studentId, courseId);

        createAttendanceForStudentAndExpectOkStatus(jwtToken, createAttendanceRequest);

        List<AttendanceDTO> resultAttendances = getAllAttendancesAndExpectOkStatus(jwtToken);

        Long attendanceId = resultAttendances.stream()
                .filter(h -> h.courseName().equals(createCourseRequest.name()))
                .map(AttendanceDTO::id)
                .findFirst()
                .orElseThrow();

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateAttendanceRequest();

        updateAttendanceAndExpectForbiddenStatus(studentJwtToken, attendanceId, updateAttendanceRequest);
    }
}
