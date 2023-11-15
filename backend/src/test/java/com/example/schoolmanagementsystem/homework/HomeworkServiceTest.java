package com.example.schoolmanagementsystem.homework;

import com.example.schoolmanagementsystem.AbstractCourseRelatedServiceTest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = HomeworkService.class)
class HomeworkServiceTest extends AbstractCourseRelatedServiceTest {
    @MockBean
    private HomeworkRepository homeworkRepository;

    @MockBean
    private HomeworkDTOMapper homeworkDTOMapper;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private AuthenticationUtil authenticationUtil;

    @MockBean
    private UpdateUtil updateUtil;

    @Autowired
    private HomeworkService homeworkService;

    private Homework createHomeworkForCourse(Course course) {
        return Homework.builder()
                .text(FAKER.lorem().sentence())
                .dueDate(LocalDate.now())
                .course(course)
                .build();
    }

    private CreateHomeworkRequest getCreateHomeworkRequest(Long courseId) {
        return new CreateHomeworkRequest(FAKER.lorem().sentence(), LocalDate.now(), courseId);
    }

    private UpdateHomeworkRequest getUpdateHomeworkRequest() {
        return new UpdateHomeworkRequest(FAKER.lorem().sentence(), LocalDate.now());
    }


    @Test
    void getAllHomeworksByAdmin() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        List<Homework> homeworks = List.of(
                createHomeworkForCourse(course),
                createHomeworkForCourse(course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(homeworkRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(homeworks, pageable, homeworks.size()));

        List<HomeworkDTO> resultHomeWorks =
                homeworkService.getAllHomeworks(pageCount, pageSize);

        assertThat(resultHomeWorks)
                .containsExactlyElementsOf(homeworks.stream().map(homeworkDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllHomeworksByTeacherItself() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long teacherId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        teacher.setId(teacherId);

        Course course = createCourseForTeacher(teacher);

        List<Homework> homeworks = List.of(
                createHomeworkForCourse(course),
                createHomeworkForCourse(course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(homeworkRepository.findByCourseTeacherId(teacherId, pageable))
                .thenReturn(new PageImpl<>(homeworks, pageable, homeworks.size()));

        List<HomeworkDTO> resultHomeWorks =
                homeworkService.getAllHomeworks(pageCount, pageSize);

        assertThat(resultHomeWorks)
                .containsExactlyElementsOf(homeworks.stream().map(homeworkDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllHomeworksByStudent() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        Long studentId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        student.setId(studentId);

        Course course = createCourseForTeacher(teacher);

        List<Homework> homeworks = List.of(
                createHomeworkForCourse(course),
                createHomeworkForCourse(course)
        );

        when(authenticationUtil.getRequestUser())
                .thenReturn(student);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(homeworkRepository.findByCourseStudentsId(studentId, pageable))
                .thenReturn(new PageImpl<>(homeworks, pageable, homeworks.size()));

        List<HomeworkDTO> resultHomeWorks =
                homeworkService.getAllHomeworks(pageCount, pageSize);

        assertThat(resultHomeWorks)
                .containsExactlyElementsOf(homeworks.stream().map(homeworkDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void addHomeworkForCourseByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseId);

        ArgumentCaptor<Homework> homeworkArgumentCaptor =
                ArgumentCaptor.forClass(Homework.class);

        homeworkService.addHomeworkForCourse(
                homeworkRequest
        );

        verify(homeworkRepository).save(homeworkArgumentCaptor.capture());

        Homework expectedHomework =
                Homework.builder()
                        .text(homeworkRequest.text())
                        .dueDate(homeworkRequest.dueDate())
                        .course(course)
                        .build();

        assertThat(homeworkArgumentCaptor.getValue())
                .isEqualTo(expectedHomework);
    }

    @Test
    void addHomeworkForCourseByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseId);

        ArgumentCaptor<Homework> homeworkArgumentCaptor =
                ArgumentCaptor.forClass(Homework.class);

        homeworkService.addHomeworkForCourse(
                homeworkRequest
        );

        verify(homeworkRepository).save(homeworkArgumentCaptor.capture());

        Homework expectedHomework =
                Homework.builder()
                        .text(homeworkRequest.text())
                        .dueDate(homeworkRequest.dueDate())
                        .course(course)
                        .build();

        assertThat(homeworkArgumentCaptor.getValue())
                .isEqualTo(expectedHomework);
    }

    @Test
    void addHomeworkForCourseByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseId);

        assertThatThrownBy(() -> homeworkService.addHomeworkForCourse(homeworkRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to add homeworks for this course");
    }

    @Test
    void addHomeworkForUnexistingCourse() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseId);

        assertThatThrownBy(() -> homeworkService.addHomeworkForCourse(homeworkRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void addHomeworkForCourseWithInsufficientAuthority() {
        Long courseId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        CreateHomeworkRequest homeworkRequest = getCreateHomeworkRequest(courseId);

        assertThatThrownBy(() -> homeworkService.addHomeworkForCourse(homeworkRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void updateHomeworkForCourseByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        ArgumentCaptor<Homework> homeworkArgumentCaptor =
                ArgumentCaptor.forClass(Homework.class);

        homeworkService.updateHomeworkForCourse(
                homeworkId,
                updateHomeworkRequest
        );

        verify(homeworkRepository).save(homeworkArgumentCaptor.capture());

        Homework expectedHomework =
                Homework.builder()
                        .text(updateHomeworkRequest.text())
                        .dueDate(updateHomeworkRequest.dueDate())
                        .course(course)
                        .build();

        assertThat(homeworkArgumentCaptor.getValue())
                .isEqualTo(expectedHomework);
    }

    @Test
    void updateHomeworkForCourseByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        ArgumentCaptor<Homework> homeworkArgumentCaptor =
                ArgumentCaptor.forClass(Homework.class);

        homeworkService.updateHomeworkForCourse(
                homeworkId,
                updateHomeworkRequest
        );

        verify(homeworkRepository).save(homeworkArgumentCaptor.capture());

        Homework expectedHomework =
                Homework.builder()
                        .text(updateHomeworkRequest.text())
                        .dueDate(updateHomeworkRequest.dueDate())
                        .course(course)
                        .build();

        assertThat(homeworkArgumentCaptor.getValue())
                .isEqualTo(expectedHomework);
    }

    @Test
    void updateHomeworkForCourseWithoutChanges() {
        User teacher = createUserByRole(Role.TEACHER);

        Long courseId = FAKER.number().randomNumber();

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.ofNullable(course));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        when(updateUtil.isFieldNullOrWithoutChange(any(), any()))
                .thenReturn(true);

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> homeworkService.updateHomeworkForCourse(
                homeworkId, updateHomeworkRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("No data changes found");
    }

    @Test
    void updateUnexistingHomework() {
        User teacher = createUserByRole(Role.TEACHER);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        Long homeworkId = FAKER.number().randomNumber();

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> homeworkService.updateHomeworkForCourse(
                homeworkId, updateHomeworkRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Homework with id [%s] does not exist".formatted(homeworkId));
    }

    @Test
    void updateHomeworkForCourseByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> homeworkService.updateHomeworkForCourse(
                homeworkId, updateHomeworkRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this homework");
    }

    @Test
    void updateHomeworkForUnexistingCourseWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        Long homeworkId = FAKER.number().randomNumber();

        UpdateHomeworkRequest updateHomeworkRequest = getUpdateHomeworkRequest();

        assertThatThrownBy(() -> homeworkService.updateHomeworkForCourse(
                homeworkId, updateHomeworkRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void deleteHomeworkForCourseByAdmin() {
        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        ArgumentCaptor<Homework> homeworkArgumentCaptor =
                ArgumentCaptor.forClass(Homework.class);

        homeworkService.deleteHomeworkForCourse(homeworkId);

        verify(homeworkRepository).delete(homeworkArgumentCaptor.capture());

        assertThat(homeworkArgumentCaptor.getValue())
                .isEqualTo(homework);
    }

    @Test
    void deleteHomeworkForCourseByTeacherItself() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        ArgumentCaptor<Homework> homeworkArgumentCaptor =
                ArgumentCaptor.forClass(Homework.class);

        homeworkService.deleteHomeworkForCourse(homeworkId);

        verify(homeworkRepository).delete(homeworkArgumentCaptor.capture());

        assertThat(homeworkArgumentCaptor.getValue())
                .isEqualTo(homework);
    }

    @Test
    void deleteUnexistingHomework() {
        User teacher = createUserByRole(Role.TEACHER);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        Long homeworkId = FAKER.number().randomNumber();

        assertThatThrownBy(() -> homeworkService.deleteHomeworkForCourse(homeworkId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Homework with id [%s] does not exist".formatted(homeworkId));
    }

    @Test
    void deleteHomeworkForCourseByAnotherTeacher() {
        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        Long homeworkId = FAKER.number().randomNumber();

        Homework homework = createHomeworkForCourse(course);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(homeworkRepository.findById(homeworkId))
                .thenReturn(Optional.ofNullable(homework));

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> homeworkService.deleteHomeworkForCourse(homeworkId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this homework");
    }

    @Test
    void deleteHomeworkForUnexistingCourseWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        Long homeworkId = FAKER.number().randomNumber();

        assertThatThrownBy(() -> homeworkService.deleteHomeworkForCourse(homeworkId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void getByCourseId() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        List<Homework> homeworks = List.of(
                createHomeworkForCourse(course),
                createHomeworkForCourse(course)
        );

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(homeworkRepository.findByCourseId(course.getId(), pageable))
                .thenReturn(new PageImpl<>(homeworks, pageable, homeworks.size()));

        List<HomeworkDTO> resultHomeWorks =
                homeworkService.getByCourseId(course.getId(), pageable);

        assertThat(resultHomeWorks)
                .containsExactlyElementsOf(homeworks.stream().map(homeworkDTOMapper).collect(Collectors.toList()));
    }
}