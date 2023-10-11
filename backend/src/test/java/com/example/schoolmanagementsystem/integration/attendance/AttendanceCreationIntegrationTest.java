package com.example.schoolmanagementsystem.integration.attendance;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.attendance.CreateAttendanceRequest;
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

public class AttendanceCreationIntegrationTest extends AbstractAttendanceIntegrationTest {
    private void createAttendanceForStudentAndExpectForbiddenStatus(
            String jwtToken,
            CreateAttendanceRequest createAttendanceRequest
    ) {
        client.post()
                .uri(ATTENDANCES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createAttendanceRequest), CreateAttendanceRequest.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminAddAttendancesForAnyStudent() throws IOException {
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

        AttendanceDTO expectedAttendance = new AttendanceDTO(
                attendanceId,
                createAttendanceRequest.type(),
                createAttendanceRequest.period(),
                attendanceDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultAttendances).contains(expectedAttendance);
    }

    @Test
    void canTeacherAddAttendancesForStudentItTeaches() throws IOException {
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

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        CreateAttendanceRequest createAttendanceRequest = getCreateAttendanceRequest(studentId, courseDTO.id());

        createAttendanceForStudentAndExpectOkStatus(teacherJwtToken, createAttendanceRequest);

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

        AttendanceDTO expectedAttendance = new AttendanceDTO(
                attendanceId,
                createAttendanceRequest.type(),
                createAttendanceRequest.period(),
                attendanceDatePublished,
                courseDTO.name(),
                courseDTO.teacherName()
        );

        assertThat(resultAttendances).contains(expectedAttendance);
    }

    @Test
    void canStudentsNotAddAttendancesForStudent() throws IOException {
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

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        CreateAttendanceRequest createAttendanceRequest = getCreateAttendanceRequest(studentId, courseId);

        createAttendanceForStudentAndExpectForbiddenStatus(studentJwtToken, createAttendanceRequest);
    }
}
