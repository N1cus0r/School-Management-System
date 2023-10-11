package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.user.Gender;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public abstract class AbstractRepositoryTest extends AbstractTestContainer{
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public CourseRepository courseRepository;

    public final Faker FAKER = new Faker();
    public User createUserByRole(Role role) {
        return userRepository.save(
                User.builder()
                        .role(role)
                        .email(FAKER.internet().safeEmailAddress())
                        .password(FAKER.internet().password())
                        .fullName(FAKER.name().lastName())
                        .gender(Gender.MALE)
                        .build()
        );
    }

    public Course createCourseForTeacher(User teacher) {
        return courseRepository.save(
                Course.builder()
                        .name(FAKER.lorem().word() + " " + FAKER.lorem().word())
                        .teacher(teacher)
                        .build()
        );
    }
}
