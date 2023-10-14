package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.user.Gender;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashSet;

@ActiveProfiles("dev")
@TestPropertySource(locations = "classpath:application.properties")
@ExtendWith(SpringExtension.class)
public abstract class AbstractServiceTest {
    public final Faker FAKER = new Faker();
    public User createUserByRole(Role role) {
        return User.builder()
                .id(FAKER.number().randomNumber())
                .role(role)
                .email(FAKER.internet().safeEmailAddress())
                .password(FAKER.crypto().sha256())
                .fullName(FAKER.name().fullName())
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.now())
                .courses(new HashSet<>())
                .build();
    }
}
