package com.example.schoolmanagementsystem.integration.user;

import com.example.schoolmanagementsystem.integration.AbstractIntegrationTest;
import com.example.schoolmanagementsystem.user.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public abstract class AbstractUserIntegrationTest extends AbstractIntegrationTest {

    User getAdminUser() {
        return userRepository.findByRole(Role.ADMIN, Pageable.ofSize(1)).getContent().get(0);
    }

}
