package com.example.schoolmanagementsystem.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record AuthenticationRequest(
   // maybe add max size ???
   @Email
   @NotEmpty(message = "username must not be empty")
   String email,
   @NotEmpty(message = "password must not be empty")
   String password
) {}
