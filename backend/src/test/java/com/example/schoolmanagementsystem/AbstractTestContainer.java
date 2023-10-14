package com.example.schoolmanagementsystem;

import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
@ActiveProfiles("dev")
@Testcontainers
public abstract class AbstractTestContainer {
    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("unit-test")
                    .withUsername("nicusor")
                    .withPassword("password");

}
