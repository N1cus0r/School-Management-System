package com.example.schoolmanagementsystem.integration;

import com.example.schoolmanagementsystem.Main;
import com.example.schoolmanagementsystem.auth.AuthenticationRequest;
import com.example.schoolmanagementsystem.auth.AuthenticationResponse;
import com.example.schoolmanagementsystem.user.*;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = Main.class
)
@TestPropertySource(locations = "classpath:application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {
    @Autowired
    public WebTestClient client;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    public UserRepository userRepository;

    public final Faker FAKER = new Faker();
    public final String AUTH_URI = "/api/v1/auth";
    public final String USERS_URI = "/api/v1/users";

    public String getRandomString() {
        return FAKER.lorem().characters(4, 5) + FAKER.lorem().word();
    }

    public String getAdminJwtToken() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:application.properties");
        InputStream inputStream = resource.getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(
                        properties.getProperty("admin.email").strip(),
                        properties.getProperty("admin.password")
                );

        return getUserJwtToken(authenticationRequest);
    }

    public String getUserJwtToken(AuthenticationRequest authenticationRequest) {
        return client.post()
                .uri(AUTH_URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult()
                .getResponseBody()
                .token();
    }

    private UserRegistrationRequest getUserRegistrationRequest(Role role) {
        return getUserRegistrationRequest(role, "");
    }

    private UserRegistrationRequest getUserRegistrationRequest(
            Role role,
            String fullName
    ) {
        return new UserRegistrationRequest(
                role,
                FAKER.internet().safeEmailAddress(),
                FAKER.internet().password(),
                fullName.isBlank() ? FAKER.name().fullName() : fullName + FAKER.name().fullName(),
                Gender.MALE,
                null,
                LocalDate.now()
        );
    }

    public UserRegistrationRequest getStudentRegistrationRequest() {
        return getUserRegistrationRequest(Role.STUDENT);
    }

    public UserRegistrationRequest getStudentRegistrationRequest(String fullName) {
        return getUserRegistrationRequest(Role.STUDENT, fullName);
    }

    public UserRegistrationRequest getTeacherRegistrationRequest() {
        return getUserRegistrationRequest(Role.TEACHER);
    }

    public UserRegistrationRequest getTeacherRegistrationRequest(String fullName) {
        return getUserRegistrationRequest(Role.TEACHER, fullName);
    }

    public UserRegistrationRequest getAdminRegistrationRequest() {
        return getUserRegistrationRequest(Role.ADMIN);
    }

    public void registerUserAndExpectOkStatus(String jwtToken, UserRegistrationRequest request) {
        client.post()
                .uri(USERS_URI + "/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(request), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    public List<UserDTO> getAllUsersByRoleAndExpectOkStatus(String jwtToken, Role role) {
        if (role.equals(Role.ADMIN)) {
            throw new RuntimeException("Invalid role. You cant retrieve admins");
        }

        return client.get()
                .uri(USERS_URI + "/" + role.name().toLowerCase() + "s")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<UserDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

    public Long getUserIdByEmailFromResultList(String userEmail, List<UserDTO> users) {
        return users.stream()
                .filter(u -> u.email().equals(userEmail))
                .map(UserDTO::id)
                .findFirst()
                .orElseThrow();
    }

    public UserDTO getUserByEmailAndExpectOkStatus(String jwtToken, String email) {
        return client.get()
                .uri(USERS_URI + "/byEmail/" + email)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<UserDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }
}
