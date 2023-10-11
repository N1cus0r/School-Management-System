package com.example.schoolmanagementsystem.integration.comment;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentRetrieveIntegrationTest extends AbstractCommentIntegrationTest {
    @Test
    void canAminGetAllComments() throws IOException {
        String jwtToken = getAdminJwtToken();

        int oldNumberOfComments = getAllCommentsAndExpectOkStatus(jwtToken).size();

        int numberOfComments = FAKER.number().numberBetween(2, 3);

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

        for (int i = 0; i < numberOfComments; i++) {
            createCommentForStudentAndExpectOkStatus(jwtToken,  getCreateCommentRequest(studentId, courseId));
        }

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(jwtToken);

        assertThat(resultComments.size())
                .isEqualTo(oldNumberOfComments + numberOfComments);
    }

    @Test
    void canTeacherGetCommentsByCoursesItTeaches() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfComments = FAKER.number().numberBetween(2, 3);

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

        for (int i = 0; i < numberOfComments; i++) {
            createCommentForStudentAndExpectOkStatus(jwtToken,  getCreateCommentRequest(studentId, courseId));
        }

        AuthenticationRequest teacherAuthenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(teacherAuthenticationRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(teacherJwtToken);

        assertThat(resultComments.size())
                .isEqualTo(numberOfComments);
    }

    @Test
    void canStudentGetCommentsByCoursesItTakes() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfCommentsBelongingToStudent = FAKER.number().numberBetween(2,3);

        int numberOfCommentsNotBelongingToStudent = FAKER.number().numberBetween(2,3);

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

        for (int i = 0; i < numberOfCommentsBelongingToStudent; i++) {
            createCommentForStudentAndExpectOkStatus(jwtToken,  getCreateCommentRequest(studentId, courseForStudentId));
        }

        for (int i = 0; i < numberOfCommentsNotBelongingToStudent; i++) {
            createCommentForStudentAndExpectOkStatus(jwtToken,  getCreateCommentRequest(anotherStudentId, courseForAnotherStudentId));
        }

        AuthenticationRequest studentAuthenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(studentAuthenticationRequest);

        List<CommentDTO> resultComments = getAllCommentsAndExpectOkStatus(studentJwtToken);

        assertThat(resultComments.size())
                .isEqualTo(numberOfCommentsBelongingToStudent);
    }
}
