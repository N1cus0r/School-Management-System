package com.example.schoolmanagementsystem.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public AuthenticationResponse login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        String token = authenticationService.login(request);
        return new AuthenticationResponse(token);
    }
}
