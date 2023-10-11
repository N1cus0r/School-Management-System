package com.example.schoolmanagementsystem.integration.attendance;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AttendanceRetrieveIntegrationTest extends AbstractAttendanceIntegrationTest {
    @Test
    void canAminGetAllAttendances() throws IOException {
        String jwtToken = getAdminJwtToken();

        int oldNumberOfAttendances = getAllAttendancesAndExpectOkStatus(jwtToken).size();

        int numberOfAttendances = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

    for (int i = 0; i < numberOfAttendances; i++) {
            createAttendanceForStudentAndExpectOkStatus(jwtToken,  getCreateAttendanceRequest(studentId, courseId));
        }

        List<AttendanceDTO> resultAttendances = getAllAttendancesAndExpectOkStatus(jwtToken);

        assertThat(resultAttendances.size())
                .isEqualTo(oldNumberOfAttendances + numberOfAttendances);
    }

    @Test
    void canTeacherGetAttendancesByCoursesItTeaches() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfAttendances = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

        for (int i = 0; i < numberOfAttendances; i++) {
            createAttendanceForStudentAndExpectOkStatus(jwtToken,  getCreateAttendanceRequest(studentId, courseId));
        }

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        List<AttendanceDTO> resultAttendances = getAllAttendancesAndExpectOkStatus(teacherJwtToken);

        assertThat(resultAttendances.size())
                .isEqualTo(numberOfAttendances);
    }

    @Test
    void canStudentGetAttendancesByCoursesItTakes() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfAttendancesBelongingToStudent = FAKER.number().numberBetween(2,3);

        int numberOfAttendancesNotBelongingToStudent = FAKER.number().numberBetween(2,3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmailAndExpectOkStatus(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseForStudentRequest = getCreateCourseRequest(teacherId);

        CreateCourseRequest createCourseForAnotherStudentRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseForStudentRequest);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseForAnotherStudentRequest);

        Long courseForStudentId = getCourseByName(jwtToken, createCourseForStudentRequest.name()).id();

        Long courseForAnotherStudentId = getCourseByName(jwtToken, createCourseForAnotherStudentRequest.name()).id();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        UserRegistrationRequest anotherStudentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, anotherStudentRegistrationRequest);

        Long studentId = getUserByEmailAndExpectOkStatus(jwtToken, studentRegistrationRequest.email()).id();

        Long anotherStudentId = getUserByEmailAndExpectOkStatus(jwtToken, anotherStudentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseForStudentId, studentId);

        addStudentToCourseAndExpectOkStatus(jwtToken, courseForAnotherStudentId, anotherStudentId);

        for (int i = 0; i < numberOfAttendancesBelongingToStudent; i++) {
            createAttendanceForStudentAndExpectOkStatus(jwtToken,  getCreateAttendanceRequest(studentId, courseForStudentId));
        }

        for (int i = 0; i < numberOfAttendancesNotBelongingToStudent; i++) {
            createAttendanceForStudentAndExpectOkStatus(jwtToken,  getCreateAttendanceRequest(anotherStudentId, courseForAnotherStudentId));
        }

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        List<AttendanceDTO> resultAttendances = getAllAttendancesAndExpectOkStatus(studentJwtToken);

        assertThat(resultAttendances.size())
                .isEqualTo(numberOfAttendancesBelongingToStudent);
    }
}
