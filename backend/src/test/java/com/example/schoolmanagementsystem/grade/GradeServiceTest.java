package com.example.schoolmanagementsystem.grade;

import com.example.schoolmanagementsystem.AbstractCourseRelatedServiceTest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.comment.*;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = GradeService.class)
class GradeServiceTest extends AbstractCourseRelatedServiceTest {
    @MockBean
    private GradeRepository gradeRepository;

    @MockBean
    private GradeDTOMapper gradeDTOMapper;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationUtil authenticationUtil;

    @MockBean
    private UpdateUtil updateUtil;

    @Autowired
    private GradeService gradeService;

    private Grade createGradeForStudent(
            User student,
            Course course
    ) {
        return Grade.builder()
                .value(FAKER.number().numberBetween(1, 10))
                .text(FAKER.lorem().sentence())
                .student(student)
                .course(course)
                .build();
    }

    private CreateGradeRequest getCreateGradeRequest(
            Long studentId,
            Long courseId
    ) {
        return new CreateGradeRequest(
                FAKER.number().numberBetween(1, 10),
                FAKER.lorem().sentence(),
                studentId,
                courseId
        );
    }

    private UpdateGradeRequest getUpdateGradeRequest() {
        return new UpdateGradeRequest(FAKER.number().numberBetween(1, 10), FAKER.lorem().sentence());
    }
    @Test
    void getAllGradesByAdmin() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.ADMIN);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Grade> grades = List.of(
                createGradeForStudent(student, course),
                createGradeForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(gradeRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(grades, pageable, grades.size()));

        List<GradeDTO> resultComments =
                gradeService.getAllGrades(pageCount, pageSize);

        assertThat(resultComments)
                .containsExactlyElementsOf(grades.stream().map(gradeDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllGradesByTeacherItself() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long teacherId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        teacher.setId(teacherId);

        User student = createUserByRole(Role.STUDENT);

        Course course = createCourseForTeacher(teacher);

        List<Grade> comments = List.of(
                createGradeForStudent(student, course),
                createGradeForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(gradeRepository.findByCourseTeacherId(teacherId, pageable))
                .thenReturn(new PageImpl<>(comments, pageable, comments.size()));

        List<GradeDTO> resultComments =
                gradeService.getAllGrades(pageCount, pageSize);

        assertThat(resultComments)
                .containsExactlyElementsOf(comments.stream().map(gradeDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllGradesByStudent() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long studentId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        student.setId(studentId);

        Course course = createCourseForTeacher(teacher);

        List<Grade> comments = List.of(
                createGradeForStudent(student, course),
                createGradeForStudent(student, course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(student);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(gradeRepository.findByCourseStudentsId(studentId, pageable))
                .thenReturn(new PageImpl<>(comments, pageable, comments.size()));

        List<GradeDTO> resultAttendances =
                gradeService.getAllGrades(pageCount, pageSize);

        assertThat(resultAttendances)
                .containsExactlyElementsOf(comments.stream().map(gradeDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void addGradeForUserByAdmin() {
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

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);

        ArgumentCaptor<Grade> gradeArgumentCaptor =
                ArgumentCaptor.forClass(Grade.class);

        gradeService.addGradeForUser(createGradeRequest);

        verify(gradeRepository).save(gradeArgumentCaptor.capture());

        Grade expectedGrade =
                Grade.builder()
                        .value(createGradeRequest.value())
                        .text(createGradeRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(gradeArgumentCaptor.getValue())
                .isEqualTo(expectedGrade);
    }

    @Test
    void addGradeForUserByTeacherItself() {
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

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);

        ArgumentCaptor<Grade> gradeArgumentCaptor =
                ArgumentCaptor.forClass(Grade.class);

        gradeService.addGradeForUser(createGradeRequest);

        verify(gradeRepository).save(gradeArgumentCaptor.capture());

        Grade expectedGrade =
                Grade.builder()
                        .value(createGradeRequest.value())
                        .text(createGradeRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(gradeArgumentCaptor.getValue())
                .isEqualTo(expectedGrade);
    }

    @Test
    void addGradeForUserByAnotherTeacher() {
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

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);

        assertThatThrownBy(() -> gradeService.addGradeForUser(createGradeRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to add grade for this course");
    }

    @Test
    void addGradeForUnexistingCourse() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);

        assertThatThrownBy(() -> gradeService.addGradeForUser(createGradeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void addGradeForUnexistingStudent() {
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

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);

        assertThatThrownBy(() -> gradeService.addGradeForUser(createGradeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(studentId));
    }

    @Test
    void addGradeForStudentNotBelongingToCourse() {
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

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);

        assertThatThrownBy(() -> gradeService.addGradeForUser(createGradeRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("Student does not belong to course");
    }

    @Test
    void addGradeWithInsufficientAuthority() {
        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        CreateGradeRequest createGradeRequest =
                getCreateGradeRequest(studentId, courseId);;

        assertThatThrownBy(() -> gradeService.addGradeForUser(createGradeRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void updateGradeForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        ArgumentCaptor<Grade> gradeArgumentCaptor =
                ArgumentCaptor.forClass(Grade.class);

        gradeService.updateGradeForUser(
                gradeId,
                updateGradeRequest
        );

        verify(gradeRepository).save(gradeArgumentCaptor.capture());

        Grade expectedGrade =
                Grade.builder()
                        .value(updateGradeRequest.value())
                        .text(updateGradeRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(gradeArgumentCaptor.getValue())
                .isEqualTo(expectedGrade);
    }

    @Test
    void updateGradeForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        ArgumentCaptor<Grade> gradeArgumentCaptor =
                ArgumentCaptor.forClass(Grade.class);

        gradeService.updateGradeForUser(
                gradeId,
                updateGradeRequest
        );

        verify(gradeRepository).save(gradeArgumentCaptor.capture());

        Grade expectedGrade =
                Grade.builder()
                        .value(updateGradeRequest.value())
                        .text(updateGradeRequest.text())
                        .student(student)
                        .course(course)
                        .build();

        assertThat(gradeArgumentCaptor.getValue())
                .isEqualTo(expectedGrade);
    }

    @Test
    void updateGradeForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        assertThatThrownBy(() -> gradeService
                .updateGradeForUser(gradeId, updateGradeRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this grade");
    }

    @Test
    void updateGradeForUserWithoutChanges() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(updateUtil.isFieldNullOrWithoutChange(any(), any()))
                .thenReturn(true);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        assertThatThrownBy(() -> gradeService
                .updateGradeForUser(gradeId, updateGradeRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("No data changes found");
    }

    @Test
    void updateUnexistingGrade() {
        Long gradeId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        assertThatThrownBy(() -> gradeService
                .updateGradeForUser(gradeId, updateGradeRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Grade with id [%s] does not exist".formatted(gradeId));
    }

    @Test
    void updateGradeForUserWithInsufficientAuthority() {
        Long gradeId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        UpdateGradeRequest updateGradeRequest = getUpdateGradeRequest();

        assertThatThrownBy(() -> gradeService
                .updateGradeForUser(gradeId, updateGradeRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void deleteGradeForUserByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        ArgumentCaptor<Grade> gradeArgumentCaptor =
                ArgumentCaptor.forClass(Grade.class);

        gradeService.deleteGradeForUser(gradeId);

        verify(gradeRepository).delete(gradeArgumentCaptor.capture());

        assertThat(gradeArgumentCaptor.getValue())
                .isEqualTo(grade);
    }

    @Test
    void deleteGradeForUserByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        ArgumentCaptor<Grade> gradeArgumentCaptor =
                ArgumentCaptor.forClass(Grade.class);

        gradeService.deleteGradeForUser(gradeId);

        verify(gradeRepository).delete(gradeArgumentCaptor.capture());

        assertThat(gradeArgumentCaptor.getValue())
                .isEqualTo(grade);
    }

    @Test
    void deleteGradeForUserByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        Long gradeId = FAKER.number().randomNumber();

        Grade grade = createGradeForStudent(student, course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(gradeRepository.findById(gradeId))
                .thenReturn(Optional.ofNullable(grade));

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> gradeService.deleteGradeForUser(gradeId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to delete this grade");
    }

    @Test
    void deleteUnexistingGrade() {
        Long gradeId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        assertThatThrownBy(() -> gradeService.deleteGradeForUser(gradeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Grade with id [%s] does not exist".formatted(gradeId));
    }

    @Test
    void deleteGradeForUserWithInsufficientAuthority() {
        Long gradeId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> gradeService.deleteGradeForUser(gradeId))
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

        List<Grade> grades = List.of(
                createGradeForStudent(student, course),
                createGradeForStudent(student, course)
        );

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(gradeRepository.findByCourseId(course.getId(), pageable))
                .thenReturn(new PageImpl<>(grades, pageable, grades.size()));

        List<GradeDTO> resultComments =
                gradeService.getByCourseId(course.getId(), pageable);

        assertThat(resultComments)
                .containsExactlyElementsOf(grades.stream().map(gradeDTOMapper).collect(Collectors.toList()));
    }
}