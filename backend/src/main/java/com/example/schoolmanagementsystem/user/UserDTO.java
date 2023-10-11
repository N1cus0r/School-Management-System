package com.example.schoolmanagementsystem.user;

import java.time.LocalDate;

public record UserDTO(
        Long id,
        String role,
        String email,
        String fullName,
        Gender gender,
        String mobilePhone,
        LocalDate dateOfBirth,
        LocalDate registrationDate,
        String profileImageId
) {}
