package com.example.schoolmanagementsystem.integration.homework;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeworkRetrieveIntegrationTest extends AbstractHomeworkIntegrationTest {

    @Test
    void canAdminGetAllHomeworks() throws IOException {
        String jwtToken = getAdminJwtToken();

        int oldNumberOfHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken).size();

        int numberOfHomeworks = FAKER.number().numberBetween(2, 3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(teacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createCourseRequest);

        Long courseId = getCourseByName(jwtToken, createCourseRequest.name()).id();

        for (int i = 0; i < numberOfHomeworks; i++) {
            createHomeworkForCourseAndExpectOkStatus(jwtToken,  getCreateHomeworkRequest(courseId));
        }

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(jwtToken);

        assertThat(resultHomeworks.size())
                .isEqualTo(oldNumberOfHomeworks + numberOfHomeworks);
    }

    @Test
    void canTeacherGetHomeworksByCoursesItTeaches() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfHomeworksBelongingToTeacher = FAKER.number().numberBetween(2,3);

        int numberOfHomeworksNotBelongingToTeacher = FAKER.number().numberBetween(2,3);

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        UserRegistrationRequest anotherTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, anotherTeacherRegistrationRequest);

        Long teacherId = getUserByEmail(jwtToken, teacherRegistrationRequest.email()).id();

        Long anotherTeacherId = getUserByEmail(jwtToken, anotherTeacherRegistrationRequest.email()).id();

        CreateCourseRequest createTeacherCourseRequest = getCreateCourseRequest(teacherId);

        CreateCourseRequest createAnotherTeacherCourseRequest = getCreateCourseRequest(anotherTeacherId);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createTeacherCourseRequest);

        createCourseForTeacherAndExpectOkStatus(jwtToken,  createAnotherTeacherCourseRequest);

        Long teacherCourseId = getCourseByName(jwtToken, createTeacherCourseRequest.name()).id();

        Long anotherTeacherCourseId = getCourseByName(jwtToken, createAnotherTeacherCourseRequest.name()).id();

        for (int i = 0; i < numberOfHomeworksBelongingToTeacher; i++) {
            createHomeworkForCourseAndExpectOkStatus(jwtToken,  getCreateHomeworkRequest(teacherCourseId));
        }

        for (int i = 0; i < numberOfHomeworksNotBelongingToTeacher; i++) {
            createHomeworkForCourseAndExpectOkStatus(jwtToken,  getCreateHomeworkRequest(anotherTeacherCourseId));
        }

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(teacherJwtToken);

        assertThat(resultHomeworks.size())
                .isEqualTo(numberOfHomeworksBelongingToTeacher);
    }

    @Test
    void canStudentGetHomeworksByCoursesItTakes() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfHomeworksBelongingToStudent = FAKER.number().numberBetween(2,3);

        int numberOfHomeworksNotBelongingToStudent = FAKER.number().numberBetween(2,3);

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

        for (int i = 0; i < numberOfHomeworksBelongingToStudent; i++) {
            createHomeworkForCourseAndExpectOkStatus(jwtToken,  getCreateHomeworkRequest(courseForStudentId));
        }

        for (int i = 0; i < numberOfHomeworksNotBelongingToStudent; i++) {
            createHomeworkForCourseAndExpectOkStatus(jwtToken,  getCreateHomeworkRequest(courseForAnotherStudentId));
        }

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        List<HomeworkDTO> resultHomeworks = getAllHomeworksAndExpectOkStatus(studentJwtToken);

        assertThat(resultHomeworks.size())
                .isEqualTo(numberOfHomeworksBelongingToStudent);
    }
}
