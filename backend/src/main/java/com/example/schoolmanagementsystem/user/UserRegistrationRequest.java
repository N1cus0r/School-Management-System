package com.example.schoolmanagementsystem.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserRegistrationRequest(
        @NotNull(message = "role must not be empty")
        Role role,
        @Email
        @NotEmpty(message = "email must not be empty")
        String email,
        @NotEmpty(message = "password must not be empty")
        String password,
        @NotEmpty(message = "name must not be empty")
        String fullName,
        @NotNull(message = "gender name must not be empty")
        Gender gender,
        String mobilePhone,
        @NotNull(message = "date of birth name must not be empty")
        LocalDate dateOfBirth
) {}
