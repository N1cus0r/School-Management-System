package com.example.schoolmanagementsystem.integration.attendance;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.attendance.CreateAttendanceRequest;
import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AttendanceDeleteIntegrationTest extends AbstractAttendanceIntegrationTest {
    private void deleteAttendanceAndExpectOkStatus(
            String jwtToken,
            Long attendanceId
    ) {
        client.delete()
                .uri(ATTENDANCES_URI + "/%s".formatted(attendanceId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void deleteAttendanceAndExpectForbiddenStatus(
            String jwtToken,
            Long attendanceId
    ) {
        client.delete()
                .uri(ATTENDANCES_URI + "/%s".formatted(attendanceId))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
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

        deleteAttendanceAndExpectOkStatus(jwtToken, attendanceId);
    }

    @Test
    void canTeacherDeleteAnyAttendanceItCreated() throws IOException {
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

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        deleteAttendanceAndExpectOkStatus(teacherJwtToken, attendanceId);
    }

    @Test
    void canStudentNotDeleteAttendance() throws IOException {
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

        deleteAttendanceAndExpectForbiddenStatus(studentJwtToken, attendanceId);
    }
}
