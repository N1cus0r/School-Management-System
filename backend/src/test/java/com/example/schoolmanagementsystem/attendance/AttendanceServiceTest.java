package com.example.schoolmanagementsystem.attendance;

import com.example.schoolmanagementsystem.AbstractCourseRelatedServiceTest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.homework.*;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.example.schoolmanagementsystem.user.UserService;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = AttendanceService.class)
class AttendanceServiceTest extends AbstractCourseRelatedServiceTest {
    @MockBean
    private AttendanceRepository attendanceRepository;

    @MockBean
    private AttendanceDTOMapper attendanceDTOMapper;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationUtil authenticationUtil;

    @MockBean
    private UpdateUtil updateUtil;

    @Autowired
    private AttendanceService attendanceService;
    private Attendance createAttendanceForStudent(
            User student,
            Course course
    ) {
        return Attendance.builder()
                .type(AttendanceType.MOTIVATED)
                .period(AttendancePeriod.LESSON_4)
                .student(student)
                .course(course)
                .build();
    }

    private CreateAttendanceRequest getCreateAttendanceRequest(
            Long studentId,
            Long courseId
    ) {
        return new CreateAttendanceRequest(
                AttendanceType.MOTIVATED,
                AttendancePeriod.LESSON_4,
                studentId,
                courseId
        );
    }

    private UpdateAttendanceRequest getUpdateHomeworkRequest() {
        return new UpdateAttendanceRequest(
                AttendanceType.LATE,
                AttendancePeriod.LESSON_2
        );
    }

    @Test
    void getAllAttendancesByAdmin() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.ADMIN);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Attendance> attendances = List.of(
                createAttendanceForStudent(student, course),
                createAttendanceForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(attendanceRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(attendances, pageable, attendances.size()));

        List<AttendanceDTO> resultAttendances =
                attendanceService.getAllAttendances(pageCount, pageSize);

        assertThat(resultAttendances)
                .containsExactlyElementsOf(attendances.stream().map(attendanceDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllAttendancesByTeacherItself() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long teacherId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        teacher.setId(teacherId);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Attendance> attendances = List.of(
                createAttendanceForStudent(student, course),
                createAttendanceForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(attendanceRepository.findByCourseTeacherId(teacherId, pageable))
                .thenReturn(new PageImpl<>(attendances, pageable, attendances.size()));

        List<AttendanceDTO> resultHomeWorks =
                attendanceService.getAllAttendances(pageCount, pageSize);

        assertThat(resultHomeWorks)
                .containsExactlyElementsOf(attendances.stream().map(attendanceDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllAttendancesByStudent() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long studentId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        student.setId(studentId);

        Course course = createCourseForTeacher(teacher);

        List<Attendance> attendances = List.of(
                createAttendanceForStudent(student, course),
                createAttendanceForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(student);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(attendanceRepository.findByCourseStudentsId(studentId, pageable))
                .thenReturn(new PageImpl<>(attendances, pageable, attendances.size()));

        List<AttendanceDTO> resultAttendances =
                attendanceService.getAllAttendances(pageCount, pageSize);

        assertThat(resultAttendances)
                .containsExactlyElementsOf(attendances.stream().map(attendanceDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void addAttendanceForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(true);

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        ArgumentCaptor<Attendance> attendanceArgumentCaptor =
                ArgumentCaptor.forClass(Attendance.class);

        attendanceService.addAttendanceForUser(createAttendanceRequest);

        verify(attendanceRepository).save(attendanceArgumentCaptor.capture());

        Attendance expectedAttendance =
                Attendance.builder()
                        .type(createAttendanceRequest.type())
                        .period(createAttendanceRequest.period())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(attendanceArgumentCaptor.getValue())
                .isEqualTo(expectedAttendance);
    }

    @Test
    void addAttendanceForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(true);

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        ArgumentCaptor<Attendance> attendanceArgumentCaptor =
                ArgumentCaptor.forClass(Attendance.class);

        attendanceService.addAttendanceForUser(createAttendanceRequest);

        verify(attendanceRepository).save(attendanceArgumentCaptor.capture());

        Attendance expectedAttendance =
                Attendance.builder()
                        .type(createAttendanceRequest.type())
                        .period(createAttendanceRequest.period())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(attendanceArgumentCaptor.getValue())
                .isEqualTo(expectedAttendance);
    }

    @Test
    void addAttendanceForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(true);

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        assertThatThrownBy(() -> attendanceService.addAttendanceForUser(createAttendanceRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to add attendance for this course");
    }

    @Test
    void addAttendanceForUnexistingCourse() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        assertThatThrownBy(() -> attendanceService.addAttendanceForUser(createAttendanceRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void addAttendanceForUnexistingStudent() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        assertThatThrownBy(() -> attendanceService.addAttendanceForUser(createAttendanceRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(studentId));
    }

    @Test
    void addAttendanceForStudentNotBelongingToCourse() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        Long studentId = FAKER.number().randomNumber();

        User student = createUserByRole(Role.STUDENT);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(userRepository.findById(studentId))
                .thenReturn(Optional.ofNullable(student));

        when(courseRepository.existsByIdAndStudentsId(courseId, studentId))
                .thenReturn(false);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        assertThatThrownBy(() -> attendanceService.addAttendanceForUser(createAttendanceRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("Student does not belong to course");
    }

    @Test
    void addAttendanceWithInsufficientAuthority() {
        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        CreateAttendanceRequest createAttendanceRequest =
                getCreateAttendanceRequest(studentId, courseId);

        assertThatThrownBy(() -> attendanceService.addAttendanceForUser(createAttendanceRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void updateAttendanceForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateHomeworkRequest();

        ArgumentCaptor<Attendance> attendanceArgumentCaptor =
                ArgumentCaptor.forClass(Attendance.class);

        attendanceService.updateAttendanceForUser(
                attendanceId,
                updateAttendanceRequest
        );

        verify(attendanceRepository).save(attendanceArgumentCaptor.capture());

        Attendance expectedAttendance =
                Attendance.builder()
                        .type(updateAttendanceRequest.type())
                        .period(updateAttendanceRequest.period())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(attendanceArgumentCaptor.getValue())
                .isEqualTo(expectedAttendance);
    }

    @Test
    void updateAttendanceForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateHomeworkRequest();

        ArgumentCaptor<Attendance> attendanceArgumentCaptor =
                ArgumentCaptor.forClass(Attendance.class);

        attendanceService.updateAttendanceForUser(
                attendanceId,
                updateAttendanceRequest
        );

        verify(attendanceRepository).save(attendanceArgumentCaptor.capture());

        Attendance expectedAttendance =
                Attendance.builder()
                        .type(updateAttendanceRequest.type())
                        .period(updateAttendanceRequest.period())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(attendanceArgumentCaptor.getValue())
                .isEqualTo(expectedAttendance);
    }

    @Test
    void updateAttendanceForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> attendanceService
                .updateAttendanceForUser(attendanceId, updateAttendanceRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this attendance");
    }

    @Test
    void updateAttendanceForUserWithoutChanges() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(updateUtil.isFieldNullOrWithoutChange(any(), any()))
                .thenReturn(true);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> attendanceService
                .updateAttendanceForUser(attendanceId, updateAttendanceRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("No data changes found");
    }

    @Test
    void updateUnexistingAttendance() {
        Long attendanceId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> attendanceService
                .updateAttendanceForUser(attendanceId, updateAttendanceRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Attendance with id [%s] does not exist".formatted(attendanceId));
    }

    @Test
    void updateAttendanceForUserWithInsufficientAuthority() {
        Long attendanceId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        UpdateAttendanceRequest updateAttendanceRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> attendanceService
                .updateAttendanceForUser(attendanceId, updateAttendanceRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void deleteAttendanceForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        ArgumentCaptor<Attendance> attendanceArgumentCaptor =
                ArgumentCaptor.forClass(Attendance.class);

        attendanceService.deleteAttendanceForUser(attendanceId);

        verify(attendanceRepository).delete(attendanceArgumentCaptor.capture());

        assertThat(attendanceArgumentCaptor.getValue())
                .isEqualTo(attendance);
    }

    @Test
    void deleteAttendanceForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        ArgumentCaptor<Attendance> attendanceArgumentCaptor =
                ArgumentCaptor.forClass(Attendance.class);

        attendanceService.deleteAttendanceForUser(attendanceId);

        verify(attendanceRepository).delete(attendanceArgumentCaptor.capture());

        assertThat(attendanceArgumentCaptor.getValue())
                .isEqualTo(attendance);
    }

    @Test
    void deleteAttendanceForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long attendanceId = FAKER.number().randomNumber();

        Attendance attendance = createAttendanceForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(attendanceRepository.findById(attendanceId))
                .thenReturn(Optional.ofNullable(attendance));

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> attendanceService.deleteAttendanceForUser(attendanceId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to delete this attendance");
    }

    @Test
    void deleteUnexistingAttendance() {
        Long attendanceId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        assertThatThrownBy(() -> attendanceService.deleteAttendanceForUser(attendanceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Attendance with id [%s] does not exist".formatted(attendanceId));
    }

    @Test
    void deleteAttendanceForUserWithInsufficientAuthority() {
        Long attendanceId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> attendanceService.deleteAttendanceForUser(attendanceId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void getByCourseId() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User teacher = createUserByRole(Role.ADMIN);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Attendance> attendances = List.of(
                createAttendanceForStudent(student, course),
                createAttendanceForStudent(student, course)
        );

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(attendanceRepository.findByCourseId(course.getId(), pageable))
                .thenReturn(new PageImpl<>(attendances, pageable, attendances.size()));

        List<AttendanceDTO> resultAttendances =
                attendanceService.getByCourseId(course.getId(), pageable);

        assertThat(resultAttendances)
                .containsExactlyElementsOf(attendances.stream().map(attendanceDTOMapper).collect(Collectors.toList()));
    }
}