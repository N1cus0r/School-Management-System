package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.integration.AbstractIntegrationTest;
import com.example.schoolmanagementsystem.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public abstract class AbstractUserIntegrationTest extends AbstractIntegrationTest {

    User getAdminUser() {
        return userRepository.findByRole(Role.ADMIN, Pageable.ofSize(1)).getContent().get(0);
    }

    LocalDate getUserRegistrationDateByEmailFromResultList(String userEmail, List<UserDTO> users) {
        return users.stream()
                .filter(u -> u.email().equals(userEmail))
                .map(UserDTO::registrationDate)
                .findFirst()
                .orElseThrow();
    }

    UserDTO getExpectedUserFromRegistrationRequest(Long userId, LocalDate userRegistrationDate, UserRegistrationRequest request) {
        return new UserDTO(
                userId,
                request.role().name(),
                request.email(),
                request.fullName(),
                request.gender(),
                request.mobilePhone(),
                request.dateOfBirth(),
                userRegistrationDate,
                null
        );
    }

    UserDTO getUserByIdResponseBodyAndExpectStatusOk(String jwtToken, Long userId) {
        return client.get()
                .uri(USERS_URI + "/{id}", userId)
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
