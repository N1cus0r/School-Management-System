package com.example.schoolmanagementsystem.integration.auth;

import com.example.schoolmanagementsystem.Main;
import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.auth.AuthenticationResponse;
import com.example.schoolmanagementsystem.integration.AbstractIntegrationTest;
import com.example.schoolmanagementsystem.jwt.JwtUtil;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = Main.class
)
@TestPropertySource(locations = "classpath:application.properties")
public class AuthenticationIntegrationTest  extends AbstractIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Test
    void canLogin() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:application.properties");
        InputStream inputStream = resource.getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);

        AuthenticationRequest invalidAuthenticationRequest =
                new AuthenticationRequest(
                        FAKER.internet().safeEmailAddress(),
                        FAKER.internet().password()
                );

        AuthenticationRequest validAuthenticationRequest =
                new AuthenticationRequest(
                        properties.getProperty("admin.email").strip(),
                        properties.getProperty("admin.password")
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
