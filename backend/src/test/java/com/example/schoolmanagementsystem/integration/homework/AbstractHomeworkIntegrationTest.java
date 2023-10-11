package com.example.schoolmanagementsystem.integration.homework;

import com.example.schoolmanagementsystem.homework.CreateHomeworkRequest;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.homework.UpdateHomeworkRequest;
import com.example.schoolmanagementsystem.integration.AbstractCourseRelatedIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class AbstractHomeworkIntegrationTest extends AbstractCourseRelatedIntegrationTest {
    static final String HOMEWORKS_URI = "api/v1/homeworks";

    CreateHomeworkRequest getCreateHomeworkRequest(Long courseId) {
        return new CreateHomeworkRequest(FAKER.lorem().sentence(), LocalDate.now(), courseId);
    }
    UpdateHomeworkRequest getUpdateHomeworkRequest() {
        return new UpdateHomeworkRequest(FAKER.lorem().sentence(), LocalDate.now());
    }

    void createHomeworkForCourseAndExpectOkStatus(
            String jwtToken,
            CreateHomeworkRequest homeworkRequest
    ) {
        client.post()
                .uri(HOMEWORKS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(homeworkRequest), CreateHomeworkRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    List<HomeworkDTO> getAllHomeworksAndExpectOkStatus(String jwtToken) {
        return client.get()
                .uri(HOMEWORKS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<HomeworkDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

}
