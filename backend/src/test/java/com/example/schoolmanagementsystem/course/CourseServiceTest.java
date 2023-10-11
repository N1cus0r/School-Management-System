package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.AbstractCourseRelatedServiceTest;
import com.example.schoolmanagementsystem.AbstractServiceTest;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.Gender;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = CourseService.class)
class CourseServiceTest extends AbstractCourseRelatedServiceTest {
    @MockBean
    private CourseDTOMapper courseDTOMapper;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationUtil authenticationUtil;
    @MockBean
    private UpdateUtil updateUtil;
    @Autowired
    private CourseService courseService;

    private Course getCourseWithTeacher(User teacher) {
        return Course.builder()
                .name(FAKER.lorem().word() + " " + FAKER.lorem().word())
                .teacher(teacher)
                .build();
    }

    private List<Course> getCoursesWithTeacher(User teacher) {
        return List.of(
                createCourseForTeacher(teacher),
                createCourseForTeacher(teacher)

        );
    }

    private CreateCourseRequest getCreateCourseRequest(Long teacherId) {
        return new CreateCourseRequest(FAKER.lorem().word() + " " + FAKER.lorem().word(), teacherId);
    }

    private UpdateCourseRequest getUpdateCourseRequest() {
        return new UpdateCourseRequest(FAKER.lorem().word() + " " + FAKER.lorem().word());
    }

    @Test
    void getCourseByNameByAdmin() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(courseRepository.findByName(course.getName()))
                .thenReturn(Optional.ofNullable(course));

        CourseDTO resultCourse = courseService.getCourseByName(course.getName());

        assertThat(resultCourse)
                .isEqualTo(courseDTOMapper.apply(course));
    }

    @Test
    void getCourseByNameWithInsufficientAuthority() {
        String courseName = FAKER.lorem().sentence();

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        assertThatThrownBy(() -> courseService.getCourseByName(courseName))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to access this resource");

    }


    @Test
    void getAllCoursesByAdminWithoutSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        List<Course> courses = getCoursesWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(courseRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(courses, pageable, courses.size()));

        List<CourseDTO> resultCourses = courseService.getAllCourses(
                "",
                pageCount,
                pageSize
        );

        assertThat(resultCourses).containsExactlyElementsOf(courses.stream().map(courseDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllCoursesByAdminWithSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);
        String prefix = FAKER.lorem().word();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        List<Course> courses = getCoursesWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(courseRepository.findByNameStartsWithIgnoreCase(prefix, pageable))
                .thenReturn(new PageImpl<>(courses, pageable, courses.size()));

        List<CourseDTO> resultCourses = courseService.getAllCourses(
                prefix,
                pageCount,
                pageSize
        );

        assertThat(resultCourses).containsExactlyElementsOf(courses.stream().map(courseDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllCoursesByTeacherWithoutSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        User teacher = createUserByRole(Role.TEACHER);

        List<Course> courses = getCoursesWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(courseRepository.findByTeacherId(teacher.getId(), pageable))
                .thenReturn(new PageImpl<>(courses, pageable, courses.size()));

        List<CourseDTO> resultCourses = courseService.getAllCourses(
                "",
                pageCount,
                pageSize
        );

        assertThat(resultCourses).containsExactlyElementsOf(courses.stream().map(courseDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllCoursesByTeacherWithSearch() {
        int pageCount = FAKER.number().numberBetween(1, 10);
        int pageSize = FAKER.number().numberBetween(1, 10);
        String prefix = FAKER.lorem().word();

        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        User teacher = createUserByRole(Role.TEACHER);

        List<Course> courses = getCoursesWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        when(courseRepository.findByTeacherIdAndNameStartsWithIgnoreCase(teacher.getId(), prefix, pageable))
                .thenReturn(new PageImpl<>(courses, pageable, courses.size()));

        List<CourseDTO> resultCourses = courseService.getAllCourses(
                prefix,
                pageCount,
                pageSize
        );

        assertThat(resultCourses).containsExactlyElementsOf(courses.stream().map(courseDTOMapper).collect(Collectors.toList()));
    }

    @Test
    void getAllCoursesWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> courseService
                .getAllCourses(
                        "",
                        FAKER.number().randomDigitNotZero(),
                        FAKER.number().randomDigitNotZero())
        )
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to access this information");
    }

    @Test
    void addCourseForUserByAdmin() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(userId);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        ArgumentCaptor<Course> courseArgumentCaptor =
                ArgumentCaptor.forClass(Course.class);

        courseService.addCourseForTeacher(createCourseRequest);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        Course expectedCourse = Course.builder()
                .name(createCourseRequest.name())
                .teacher(teacher)
                .build();

        assertThat(courseArgumentCaptor.getValue())
                .isEqualTo(expectedCourse);
    }

    @Test
    void addCourseForUnexistingUser() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        admin.setId(userId);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        assertThatThrownBy(() -> courseService.addCourseForTeacher(getCreateCourseRequest(userId)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(userId));

    }

    @Test
    void addCourseForUserByTeacherItself() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        teacher.setId(userId);

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(userId);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        ArgumentCaptor<Course> courseArgumentCaptor =
                ArgumentCaptor.forClass(Course.class);

        courseService.addCourseForTeacher(createCourseRequest);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        Course expectedCourse = Course.builder()
                .name(createCourseRequest.name())
                .teacher(teacher)
                .build();

        assertThat(courseArgumentCaptor.getValue())
                .isEqualTo(expectedCourse);
    }

    @Test
    void addCourseForUserByAnotherTeacher() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        CreateCourseRequest createCourseRequest = getCreateCourseRequest(userId);

        when(authenticationUtil.getRequestUser())
                .thenReturn(teacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(anotherTeacher));

        assertThatThrownBy(() -> courseService.addCourseForTeacher(createCourseRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to create courses for other teachers");

    }

    @Test
    void addCourseForUserWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> courseService.addCourseForTeacher(
                        getCreateCourseRequest(FAKER.number().randomNumber())
                )
        )
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void updateUserCourseByAdmin() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        UpdateCourseRequest updateCourseRequest = getUpdateCourseRequest();

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        ArgumentCaptor<Course> courseArgumentCaptor =
                ArgumentCaptor.forClass(Course.class);

        courseService.updateTeacherCourse(courseId, updateCourseRequest);

        verify(courseRepository).save(courseArgumentCaptor.capture());

        Course expectedCourse = Course.builder()
                .name(updateCourseRequest.name())
                .teacher(teacher)
                .build();

        assertThat(courseArgumentCaptor.getValue())
                .isEqualTo(expectedCourse);
    }

    @Test
    void updateUserCourseWithNoChanges() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        UpdateCourseRequest updateCourseRequest = getUpdateCourseRequest();

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(updateUtil.isFieldNullOrWithoutChange(any(), any()))
                .thenReturn(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.updateTeacherCourse(courseId, updateCourseRequest))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("No data changes found");
    }



    @Test
    void updateUnexistingCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        UpdateCourseRequest updateCourseRequest = getUpdateCourseRequest();

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> courseService.updateTeacherCourse(courseId, updateCourseRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));

    }
    @Test
    void updateUserCourseByAnotherTeacher() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        UpdateCourseRequest updateCourseRequest = getUpdateCourseRequest();

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.updateTeacherCourse(courseId, updateCourseRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to update this course");
    }

    @Test
    void updateUserCourseWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        Long courseId = FAKER.number().randomNumber();

        UpdateCourseRequest updateCourseRequest = getUpdateCourseRequest();

        assertThatThrownBy(() -> courseService.updateTeacherCourse(courseId, updateCourseRequest))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void deleteUserCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        ArgumentCaptor<Course> courseArgumentCaptor =
                ArgumentCaptor.forClass(Course.class);

        courseService.deleteUserCourse(courseId);

        verify(courseRepository).delete(courseArgumentCaptor.capture());

        assertThat(courseArgumentCaptor.getValue())
                .isEqualTo(course);
    }

    @Test
    void deleteUnexistingCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> courseService.deleteUserCourse(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void deleteUserCourseByAnotherTeacher() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long userId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.deleteUserCourse(courseId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to delete this course");
    }

    @Test
    void deleteUserCourseWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        Long courseId = FAKER.number().randomNumber();

        assertThatThrownBy(() -> courseService.deleteUserCourse(courseId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void addStudentToCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        when(userRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        courseService.addStudentToCourse(courseId, studentId);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getCourses())
                .contains(course);
    }

    @Test
    void addUnexistingStudentToCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.addStudentToCourse(courseId, studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(studentId));
    }

    @Test
    void addInvalidStudentToCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        when(userRepository.findById(studentId))
                .thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> courseService.addStudentToCourse( courseId, studentId))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("You can't add courses to user with id [%s]".formatted(studentId));
    }


    @Test
    void addStudentToUnexistingCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> courseService.addStudentToCourse( courseId, studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void addStudentToCourseByAnotherTeacher() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.addStudentToCourse( courseId, studentId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to add students to this course");
    }

    @Test
    void addStudentToCourseWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> courseService.addStudentToCourse( courseId, studentId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }

    @Test
    void removeStudentFrom() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        User student = createUserByRole(Role.STUDENT);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        when(userRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        courseService.removeStudentFromCourse( courseId, studentId);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getCourses())
                .doesNotContain(course);
    }

    @Test
    void removeStudentFromUnexistingCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> courseService.removeStudentFromCourse( courseId, studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Course with id [%s] does not exist".formatted(courseId));
    }

    @Test
    void removeUnexistingStudentFromCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.removeStudentFromCourse( courseId, studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id [%s] does not exist".formatted(studentId));
    }

    @Test
    void removeInvalidStudentFromCourse() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User admin = createUserByRole(Role.ADMIN);

        User teacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(admin);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(true);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        when(userRepository.findById(studentId))
                .thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> courseService.removeStudentFromCourse( courseId, studentId))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("You can't add courses to user with id [%s]".formatted(studentId));
    }

    @Test
    void removeStudentFromCourseByAnotherTeacher() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long teacherId = FAKER.number().randomNumber();

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        User teacher = createUserByRole(Role.TEACHER);

        User anotherTeacher = createUserByRole(Role.TEACHER);

        Course course = getCourseWithTeacher(teacher);

        when(authenticationUtil.getRequestUser())
                .thenReturn(anotherTeacher);

        when(authenticationUtil.isUserAdmin())
                .thenReturn(false);

        when(userRepository.findById(teacherId))
                .thenReturn(Optional.of(teacher));

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.removeStudentFromCourse( courseId, studentId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right to remove students from this course");
    }

    @Test
    void removeStudentFromCourseWithInsufficientAuthority() {
        when(authenticationUtil.isUserStudent())
                .thenReturn(false);

        Long courseId = FAKER.number().randomNumber();

        Long studentId = FAKER.number().randomNumber();

        when(authenticationUtil.isUserStudent())
                .thenReturn(true);

        assertThatThrownBy(() -> courseService.removeStudentFromCourse( courseId, studentId))
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("You don't have the right use this service");
    }
}