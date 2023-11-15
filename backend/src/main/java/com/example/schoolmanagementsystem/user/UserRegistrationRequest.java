package com.example.schoolmanagementsystem.user;

import com.example.schoolmanagementsystem.util.annotations.NullableNotBlank;
import jakarta.validation.constraints.*;

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

        @NullableNotBlank
        String mobilePhone,
        LocalDate dateOfBirth
) {
}
