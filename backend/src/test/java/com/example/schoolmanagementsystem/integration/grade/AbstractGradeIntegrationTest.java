package com.example.schoolmanagementsystem.integration.grade;

import com.example.schoolmanagementsystem.grade.CreateGradeRequest;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.grade.UpdateGradeRequest;
import com.example.schoolmanagementsystem.integration.AbstractCourseRelatedIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class AbstractGradeIntegrationTest extends AbstractCourseRelatedIntegrationTest {
    static final String GRADES_URI = "/api/v1/grades";

    CreateGradeRequest getCreateGradeRequest(
            Long studentId,
            Long courseId
    ) {
        return new CreateGradeRequest(
                FAKER.number().numberBetween(1, 10),
                FAKER.lorem().sentence(),
                studentId,
                courseId
        );
    }

    UpdateGradeRequest getUpdateGradeRequest() {
        return new UpdateGradeRequest(FAKER.number().numberBetween(1, 10), FAKER.lorem().sentence());
    }

    List<GradeDTO> getAllGradesAndExpectOkStatus(String jwtToken) {
        return client.get()
                .uri(GRADES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<GradeDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

    void createGradeForStudentAndExpectOkStatus(
            String jwtToken,
            CreateGradeRequest createGradeRequest
    ) {
        client.post()
                .uri(GRADES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createGradeRequest), CreateGradeRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
