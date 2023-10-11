package com.example.schoolmanagementsystem.integration.grade;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GradeRetrieveIntegrationTest extends AbstractGradeIntegrationTest {
    @Test
    void canAminGetAllGrades() throws IOException {
        String jwtToken = getAdminJwtToken();

        int oldNumberOfGrades = getAllGradesAndExpectOkStatus(jwtToken).size();

        int numberOfGrades = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

        for (int i = 0; i < numberOfGrades; i++) {
            createGradeForStudentAndExpectOkStatus(jwtToken,  getCreateGradeRequest(studentId, courseId));
        }

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(jwtToken);

        assertThat(resultGrades.size())
                .isEqualTo(oldNumberOfGrades + numberOfGrades);
    }

    @Test
    void canTeacherGetGradesByCoursesItTeaches() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfGrades = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseId, studentId);

        for (int i = 0; i < numberOfGrades; i++) {
            createGradeForStudentAndExpectOkStatus(jwtToken,  getCreateGradeRequest(studentId, courseId));
        }

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(teacherJwtToken);

        assertThat(resultGrades.size())
                .isEqualTo(numberOfGrades);
    }

    @Test
    void canStudentGetGradesByCoursesItTakes() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfGradesBelongingToStudent = FAKER.number().numberBetween(2,3);

        int numberOfGradesNotBelongingToStudent = FAKER.number().numberBetween(2,3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

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

        Long studentId = getUserByEmail(jwtToken, studentRegistrationRequest.email()).id();

        Long anotherStudentId = getUserByEmail(jwtToken, anotherStudentRegistrationRequest.email()).id();

        addStudentToCourseAndExpectOkStatus(jwtToken, courseForStudentId, studentId);

        addStudentToCourseAndExpectOkStatus(jwtToken, courseForAnotherStudentId, anotherStudentId);

        for (int i = 0; i < numberOfGradesBelongingToStudent; i++) {
            createGradeForStudentAndExpectOkStatus(jwtToken,  getCreateGradeRequest(studentId, courseForStudentId));
        }

        for (int i = 0; i < numberOfGradesNotBelongingToStudent; i++) {
            createGradeForStudentAndExpectOkStatus(jwtToken,  getCreateGradeRequest(anotherStudentId, courseForAnotherStudentId));
        }

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        List<GradeDTO> resultGrades = getAllGradesAndExpectOkStatus(studentJwtToken);

        assertThat(resultGrades.size())
                .isEqualTo(numberOfGradesBelongingToStudent);
    }
}
