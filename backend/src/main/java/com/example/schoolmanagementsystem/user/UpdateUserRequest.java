package com.example.schoolmanagementsystem.user;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Email
        String email,
        String fullName,
        Gender gender,
        String mobilePhone,
        LocalDate dateOfBirth
) {}
