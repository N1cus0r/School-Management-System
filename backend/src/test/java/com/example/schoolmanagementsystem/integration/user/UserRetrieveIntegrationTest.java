package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.UserDTO;
import com.example.schoolmanagementsystem.user.UserDTOMapper;
import com.example.schoolmanagementsystem.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class UserRetrieveIntegrationTest extends AbstractUserIntegrationTest {
    @Autowired
    private UserDTOMapper userDTOMapper;

    private void getUserByIdAndExpectStatusForbidden(String jwtToken, Long userId) {
        client.get()
                .uri(USERS_URI + "/{id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private List<UserDTO> getUsersByRoleAndSearchByFullNameAndExpectOkStatus(
            String jwtToken,
            Role role,
            String prefix
    ) {
        return client.get()
                .uri(USERS_URI + "/" + role.name().toLowerCase() + "s" + "?fullNameSearch=" + prefix)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<UserDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

    private void getUsersByRoleAndSearchByFullNameAndExpectForbiddenStatus(
            String jwtToken,
            Role role,
            String prefix
    ) {
        client.get()
                .uri(USERS_URI + "/" + role.name().toLowerCase() + "s" + "?fullNameSearch=" + prefix)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void canAdminGetTeacherAndStudentAndItselfById() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        List<UserDTO> students = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.STUDENT);

        Long studentId = getUserIdByEmailFromResultList(
                studentRegistrationRequest.email(),
                students
        );

        LocalDate studentRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        studentRegistrationRequest.email(),
                        students
                );

        UserDTO expectedStudent = getExpectedUserFromRegistrationRequest(
                studentId,
                studentRegistrationDate,
                studentRegistrationRequest
        );

        List<UserDTO> teachers = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.TEACHER);

        Long teacherId = getUserIdByEmailFromResultList(
                teacherRegistrationRequest.email(),
                teachers
        );

        LocalDate teacherRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        teacherRegistrationRequest.email(),
                        teachers
                );

        UserDTO expectedTeacher =
                getExpectedUserFromRegistrationRequest(
                        teacherId,
                        teacherRegistrationDate,
                        teacherRegistrationRequest
                );

        UserDTO expectedAmin = userDTOMapper.apply(getAdminUser());

        Long adminId = expectedAmin.id();

        UserDTO resultStudent = getUserByIdResponseBodyAndExpectStatusOk(jwtToken, studentId);

        UserDTO resultTeacher = getUserByIdResponseBodyAndExpectStatusOk(jwtToken, teacherId);

        UserDTO resultAdmin = getUserByIdResponseBodyAndExpectStatusOk(jwtToken, adminId);

        assertThat(resultStudent).isEqualTo(expectedStudent);

        assertThat(resultTeacher).isEqualTo(expectedTeacher);

        assertThat(resultAdmin).isEqualTo(expectedAmin);

    }

    @Test
    void canTeacherGetStudentAndItselfById() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest invalidTeacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, invalidTeacherRegistrationRequest);


        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        List<UserDTO> students = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.STUDENT);

        Long studentId = getUserIdByEmailFromResultList(
                studentRegistrationRequest.email(),
                students
        );

        LocalDate studentRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        studentRegistrationRequest.email(),
                        students
                );

        UserDTO expectedStudent = getExpectedUserFromRegistrationRequest(
                studentId,
                studentRegistrationDate,
                studentRegistrationRequest
        );

        List<UserDTO> teachers = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.TEACHER);

        Long teacherId = getUserIdByEmailFromResultList(
                teacherRegistrationRequest.email(),
                teachers
        );

        LocalDate teacherRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        teacherRegistrationRequest.email(),
                        teachers
                );

        UserDTO expectedTeacher =
                getExpectedUserFromRegistrationRequest(
                        teacherId,
                        teacherRegistrationDate,
                        teacherRegistrationRequest
                );

        Long invalidTeacherId = getUserIdByEmailFromResultList(
                invalidTeacherRegistrationRequest.email(),
                teachers
        );

        UserDTO resultStudent = getUserByIdResponseBodyAndExpectStatusOk(teacherJwtToken, studentId);

        UserDTO resultTeacher = getUserByIdResponseBodyAndExpectStatusOk(teacherJwtToken, teacherId);

        getUserByIdAndExpectStatusForbidden(teacherJwtToken, invalidTeacherId);

        assertThat(resultStudent).isEqualTo(expectedStudent);

        assertThat(resultTeacher).isEqualTo(expectedTeacher);
    }

    @Test
    void canStudentGetItselfById() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        UserRegistrationRequest invalidStudentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        registerUserAndExpectOkStatus(jwtToken, invalidStudentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        List<UserDTO> students = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.STUDENT);

        Long studentId = getUserIdByEmailFromResultList(
                studentRegistrationRequest.email(),
                students
        );

        LocalDate studentRegistrationDate =
                getUserRegistrationDateByEmailFromResultList(
                        studentRegistrationRequest.email(),
                        students
                );

        UserDTO expectedStudent = getExpectedUserFromRegistrationRequest(
                studentId,
                studentRegistrationDate,
                studentRegistrationRequest
        );

        List<UserDTO> teachers = getAllUsersByRoleAndExpectOkStatus(jwtToken, Role.TEACHER);

        Long teacherId = getUserIdByEmailFromResultList(
                teacherRegistrationRequest.email(),
                teachers
        );

        Long invalidStudentId = getUserIdByEmailFromResultList(
                invalidStudentRegistrationRequest.email(),
                students
        );

        UserDTO resultStudent = getUserByIdResponseBodyAndExpectStatusOk(studentJwtToken, studentId);

        getUserByIdAndExpectStatusForbidden(studentJwtToken, teacherId);

        getUserByIdAndExpectStatusForbidden(studentJwtToken, invalidStudentId);

        assertThat(resultStudent).isEqualTo(expectedStudent);
    }

    @Test
    void canAdminSearchStudentsByFullName() throws IOException {
        String jwtToken = getAdminJwtToken();

        int numberOfStudentsMatchingPrefix = FAKER.number().numberBetween(2,3);

        int numberOfStudentsNotMatchingPrefix = FAKER.number().numberBetween(2,3);

        String prefix = FAKER.lorem().word();

        for (int i = 0; i < numberOfStudentsMatchingPrefix; i++) {
            registerUserAndExpectOkStatus(jwtToken, getStudentRegistrationRequest(prefix));
        }

        for (int i = 0; i < numberOfStudentsNotMatchingPrefix; i++) {
            registerUserAndExpectOkStatus(jwtToken, getStudentRegistrationRequest());
        }

        List<UserDTO> studentsMatchingPrefix =
                getUsersByRoleAndSearchByFullNameAndExpectOkStatus(jwtToken, Role.STUDENT, prefix);

        assertThat(studentsMatchingPrefix.size())
                .isEqualTo(numberOfStudentsMatchingPrefix);
    }

    @Test
    void canTeacherSearchStudentsByFullName() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        int numberOfStudentsMatchingPrefix = FAKER.number().numberBetween(2,3);

        int numberOfStudentsNotMatchingPrefix = FAKER.number().numberBetween(2,3);

        String prefix = FAKER.lorem().word();

        for (int i = 0; i < numberOfStudentsMatchingPrefix; i++) {
            registerUserAndExpectOkStatus(jwtToken, getStudentRegistrationRequest(prefix));
        }

        for (int i = 0; i < numberOfStudentsNotMatchingPrefix; i++) {
            registerUserAndExpectOkStatus(jwtToken, getStudentRegistrationRequest());
        }

        List<UserDTO> studentsMatchingPrefix =
                getUsersByRoleAndSearchByFullNameAndExpectOkStatus(teacherJwtToken, Role.STUDENT, prefix);

        assertThat(studentsMatchingPrefix.size())
                .isEqualTo(numberOfStudentsMatchingPrefix);
    }

    @Test
    void canStudentNotSearchStudentsByFullName() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        getUsersByRoleAndSearchByFullNameAndExpectForbiddenStatus(studentJwtToken, Role.STUDENT, FAKER.lorem().word());
    }

    @Test
    void canAdminSearchTeachersByFullName() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        int numberOfTeachersMatchingPrefix = FAKER.number().numberBetween(2,3);

        int numberOfTeachersNotMatchingPrefix = FAKER.number().numberBetween(2,3);

        String prefix = getRandomString();

        for (int i = 0; i < numberOfTeachersMatchingPrefix; i++) {
            registerUserAndExpectOkStatus(jwtToken, getTeacherRegistrationRequest(prefix));
        }

        for (int i = 0; i < numberOfTeachersNotMatchingPrefix; i++) {
            registerUserAndExpectOkStatus(jwtToken, getTeacherRegistrationRequest());
        }

        List<UserDTO> teachersMatchingPrefix =
                getUsersByRoleAndSearchByFullNameAndExpectOkStatus(jwtToken, Role.TEACHER, prefix);

        assertThat(teachersMatchingPrefix.size())
                .isEqualTo(numberOfTeachersMatchingPrefix);
    }

    @Test
    void canTeacherNotSearchTeachersByFullName() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest teacherRegistrationRequest =
                getTeacherRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, teacherRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        teacherRegistrationRequest.email(),
                        teacherRegistrationRequest.password()
                );

        String teacherJwtToken = getUserJwtToken(authenticationRequest);

        getUsersByRoleAndSearchByFullNameAndExpectForbiddenStatus(teacherJwtToken, Role.TEACHER, FAKER.lorem().word());
    }

    @Test
    void canStudentNotSearchTeachersByFullName() throws IOException {
        String jwtToken = getAdminJwtToken();

        UserRegistrationRequest studentRegistrationRequest =
                getStudentRegistrationRequest();

        registerUserAndExpectOkStatus(jwtToken, studentRegistrationRequest);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        studentRegistrationRequest.email(),
                        studentRegistrationRequest.password()
                );

        String studentJwtToken = getUserJwtToken(authenticationRequest);

        getUsersByRoleAndSearchByFullNameAndExpectForbiddenStatus(studentJwtToken, Role.TEACHER, FAKER.lorem().word());
    }
}
