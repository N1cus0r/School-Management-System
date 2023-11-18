package com.example.schoolmanagementsystem.user;

import com.example.schoolmanagementsystem.AbstractCourseDependentEntityRepositoryTest;
import com.example.schoolmanagementsystem.AbstractRepositoryTest;
import com.example.schoolmanagementsystem.AbstractTestContainer;
import com.example.schoolmanagementsystem.Main;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


class UserRepositoryTest extends AbstractCourseDependentEntityRepositoryTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    private CourseRepository courseRepository;
    private final Faker FAKER = new Faker();

    @Test
    void findByEmail() {
        User user =  User.builder()
                .role(Role.STUDENT)
                .email(FAKER.internet().safeEmailAddress())
                .password(FAKER.internet().password())
                .fullName(FAKER.name().fullName())
                .gender(Gender.MALE)
                .build();

        repository.save(user);

        User resultUser = repository.findByEmail(user.getEmail())
                .orElseThrow();

        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void findByRole() {
        int numberOfStudents = 10;
        Role role = Role.STUDENT;

        for (int i = 0; i < numberOfStudents; i++) {
            User user = User.builder()
                    .role(role)
                    .email(FAKER.internet().safeEmailAddress())
                    .password(FAKER.internet().password())
                    .fullName(FAKER.name().fullName())
                    .gender(Gender.MALE)
                    .build();

            repository.save(user);
        }


        Pageable page = PageRequest.of(0, numberOfStudents + 1);

        Page<User> resultUserPage = repository.findByRole(role, page);

        assertThat(resultUserPage.getContent().size())
                .isEqualTo(numberOfStudents);
    }

    @Test
    void findByRoleAndFullNameStartsWithIgnoreCase() {
        int numberOfStudentsSatisfyingCondition = 5;
        int numberOfStudentsNotSatisfyingCondition = 5;
        String firstName = FAKER.name().firstName();

        Role role = Role.STUDENT;

        for (int i = 0; i < numberOfStudentsSatisfyingCondition; i++) {
            repository.save(User.builder()
                    .role(role)
                    .email(FAKER.internet().safeEmailAddress())
                    .password(FAKER.internet().password())
                    .fullName(firstName + " " + FAKER.name().lastName())
                    .gender(Gender.MALE)
                    .build());
        }

        for (int i = 0; i < numberOfStudentsNotSatisfyingCondition; i++) {
            repository.save(User.builder()
                    .role(role)
                    .email(FAKER.internet().safeEmailAddress())
                    .password(FAKER.internet().password())
                    .fullName(FAKER.name().fullName())
                    .gender(Gender.MALE)
                    .build());
        }

        Pageable page = PageRequest.of(
                0,
                numberOfStudentsSatisfyingCondition + numberOfStudentsNotSatisfyingCondition);

        Page<User> resultUserPage =
                repository.findByRoleAndFullNameStartsWithIgnoreCase(
                    role, firstName, page
        );

        assertThat(resultUserPage.getContent().size())
                .isEqualTo(numberOfStudentsSatisfyingCondition);
    }

    @Test
    void existsByEmail() {
        User user =  User.builder()
                .role(Role.STUDENT)
                .email(FAKER.internet().safeEmailAddress())
                .password(FAKER.internet().password())
                .fullName(FAKER.name().fullName())
                .gender(Gender.MALE)
                .build();

        repository.save(user);

        boolean existsByEmail = repository.existsByEmail(user.getEmail());

        assertThat(existsByEmail).isTrue();
    }

    @Test
    void existsByEmailWillReturnFalse() {
        String email = FAKER.internet().safeEmailAddress();

        boolean existsByEmail = repository.existsByEmail(email);

        assertThat(existsByEmail).isFalse();
    }

    @Test
    void existsByRole() {
        User user =  User.builder()
                .role(Role.STUDENT)
                .email(FAKER.internet().safeEmailAddress())
                .password(FAKER.internet().password())
                .fullName(FAKER.name().fullName())
                .gender(Gender.MALE)
                .build();

        repository.save(user);

        boolean existsByRole = repository.existsByRole(user.getRole());

        assertThat(existsByRole).isTrue();
    }

    @Test
    void existsByRoleWillReturnFalse() {
        boolean existsByRole = repository.existsByRole(Role.STUDENT);

        assertThat(existsByRole).isFalse();
    }

    @Test
    void findByCoursesId() {
        int numberOfStudentsSatisfyingCondition = 5;
        int numberOfStudentsNotSatisfyingCondition = 5;

        Set<User> studentsTakingCourse = new HashSet<>();

        for (int i = 0; i < numberOfStudentsSatisfyingCondition; i++) {
            studentsTakingCourse.add(createUserByRole(Role.STUDENT));
        }


        for (int i = 0; i < numberOfStudentsNotSatisfyingCondition; i++) {
            createUserByRole(Role.STUDENT);
        }

        Course course = createCourseForTeacherWithStudents(
                createUserByRole(Role.TEACHER),
                studentsTakingCourse
        );

        Pageable page = PageRequest.of(
                0,
                numberOfStudentsSatisfyingCondition + numberOfStudentsNotSatisfyingCondition);

        Page<User> resultUserPage =
                repository.findByCoursesId(
                        course.getId(), page
                );

        assertThat(resultUserPage.getContent().size())
                .isEqualTo(numberOfStudentsSatisfyingCondition);
    }
}