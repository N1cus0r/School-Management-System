package com.example.schoolmanagementsystem.integration.auth;

import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.integration.AbstractIntegrationTest;
import com.example.schoolmanagementsystem.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Test
    void canLogin() throws IOException {
        AuthenticationRequest invalidAuthenticationRequest =
                new AuthenticationRequest(
                        FAKER.internet().safeEmailAddress(),
                        FAKER.internet().password()
                );

        AuthenticationRequest validAuthenticationRequest =
                new AuthenticationRequest(
                        adminEmail,
                        adminPassword
                );

        client.post()
                .uri(AUTH_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(invalidAuthenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();


        String jwtToken = getUserJwtToken(validAuthenticationRequest);

        assertThat(jwtUtil.isTokenValid(jwtToken, validAuthenticationRequest.email()))
                .isTrue();
    }
}
