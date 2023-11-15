package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.AbstractRepositoryTest;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;


class CourseRepositoryTest extends AbstractRepositoryTest {

    private Course createCourseForTeacherWithNamePrefix(User teacher, String prefix) {
        return courseRepository.save(
                Course.builder()
                        .name(prefix + FAKER.lorem().word() + " " + FAKER.lorem().word())
                        .teacher(teacher)
                        .build()
        );
    }

    private void assignCourseToStudent(Course course, User student) {
        student.getCourses().add(course);
        userRepository.save(student);
    }

    @Test
    void findByName() {
        User teacher = createUserByRole(Role.TEACHER);

        String name = FAKER.lorem().word() + " " + FAKER.lorem().word();

        Course course = courseRepository.save(
                Course.builder()
                        .name(name)
                        .teacher(teacher)
                        .build()
        );

        Course resultCourse = courseRepository.findByName(name)
                .orElseThrow();

        assertThat(resultCourse).isEqualTo(course);
    }

    @Test
    void findByNameStartsWithIgnoreCase() {
        User teacher = createUserByRole(Role.TEACHER);

        int numberOfCoursesSatisfyingCondition = 5;
        int numberOfCoursesNotSatisfyingCondition = 5;

        String prefix = FAKER.lorem().word();

        for (int i = 0; i < numberOfCoursesSatisfyingCondition; i++) {
            createCourseForTeacherWithNamePrefix(teacher, prefix);
        }

        for (int i = 0; i < numberOfCoursesNotSatisfyingCondition; i++) {
            createCourseForTeacher(teacher);
        }

        Pageable page = PageRequest.of(
                0,
                numberOfCoursesSatisfyingCondition + numberOfCoursesNotSatisfyingCondition);

        Page<Course> resultCoursePage =
                courseRepository.findByNameStartsWithIgnoreCase(
                        prefix, page
                );

        assertThat(resultCoursePage.getContent().size())
                .isEqualTo(numberOfCoursesSatisfyingCondition);
    }

    @Test
    void findByTeacherId() {
        User teacherA = createUserByRole(Role.TEACHER);

        User teacherB = createUserByRole(Role.TEACHER);

        int numberOfCoursesSatisfyingCondition = FAKER.number().numberBetween(2, 3);
        int numberOfCoursesNotSatisfyingCondition = FAKER.number().numberBetween(2, 3);

        for (int i = 0; i < numberOfCoursesSatisfyingCondition; i++) {
            createCourseForTeacher(teacherA);
        }

        for (int i = 0; i < numberOfCoursesNotSatisfyingCondition; i++) {
            createCourseForTeacher(teacherB);
        }

        Pageable page = PageRequest.of(
                0,
                numberOfCoursesSatisfyingCondition + numberOfCoursesNotSatisfyingCondition);

        Page<Course> resultCoursePage =
                courseRepository.findByTeacherId(
                        teacherA.getId(), page
                );

        assertThat(resultCoursePage.getContent().size())
                .isEqualTo(numberOfCoursesSatisfyingCondition);
    }

    @Test
    void findByTeacherIdAndNameStartsWithIgnoreCase() {
        User teacherA = createUserByRole(Role.TEACHER);

        User teacherB = createUserByRole(Role.TEACHER);

        String prefix = FAKER.lorem().word();

        int numberOfCoursesSatisfyingCondition = FAKER.number().numberBetween(2, 3);
        int numberOfCoursesNotSatisfyingCondition = FAKER.number().numberBetween(2, 3);

        for (int i = 0; i < numberOfCoursesSatisfyingCondition; i++) {
            createCourseForTeacherWithNamePrefix(teacherA, prefix);
        }

        for (int i = 0; i < numberOfCoursesNotSatisfyingCondition; i++) {
            createCourseForTeacher(teacherB);
        }

        Pageable page = PageRequest.of(
                0,
                numberOfCoursesSatisfyingCondition + numberOfCoursesNotSatisfyingCondition);

        Page<Course> resultCoursePage =
                courseRepository.findByTeacherIdAndNameStartsWithIgnoreCase(
                        teacherA.getId(), prefix, page
                );

        assertThat(resultCoursePage.getContent().size())
                .isEqualTo(numberOfCoursesSatisfyingCondition);
    }

    @Test
    void existsByIdAndStudentsIdShouldReturnTrue() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        User student = createUserByRole(Role.STUDENT);

        assignCourseToStudent(course, student);

        boolean isStudentAssignedToCourse =
                courseRepository.existsByIdAndStudentsId(course.getId(), student.getId());

        assertThat(isStudentAssignedToCourse).isTrue();
    }

    @Test
    void existsByIdAndStudentsIdShouldReturnFalse() {
        Long unexistingCourseId = FAKER.number().randomNumber();

        Long unexistingStudentId = FAKER.number().randomNumber();

        boolean isStudentAssignedToCourse =
                courseRepository.existsByIdAndStudentsId(
                        unexistingCourseId, unexistingStudentId);

        assertThat(isStudentAssignedToCourse).isFalse();
    }

    @Test
    void existsByNameShouldReturnTrue() {
        User teacher = createUserByRole(Role.TEACHER);

        Course course = createCourseForTeacher(teacher);

        boolean existsByName =
                courseRepository.existsByName(course.getName());

        assertThat(existsByName).isTrue();
    }

    @Test
    void existsByNameShouldReturnFalse() {
        String unexistingCourseName = FAKER.lorem().sentence();

        boolean existsByName =
                courseRepository.existsByName(unexistingCourseName);

        assertThat(existsByName).isFalse();
    }
}