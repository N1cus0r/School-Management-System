package com.example.schoolmanagementsystem.integration.comment;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.attendance.CreateAttendanceRequest;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.comment.CreateCommentRequest;
import com.example.schoolmanagementsystem.comment.UpdateCommentRequest;
import com.example.schoolmanagementsystem.integration.AbstractCourseRelatedIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class AbstractCommentIntegrationTest extends AbstractCourseRelatedIntegrationTest {
    static final String COMMENTS_URI = "/api/v1/comments";

    CreateCommentRequest getCreateCommentRequest(
            Long studentId,
            Long courseId
    ) {
        return new CreateCommentRequest(
                FAKER.lorem().sentence(),
                studentId,
                courseId
        );
    }

    UpdateCommentRequest getUpdateCommentRequest() {
        return new UpdateCommentRequest(FAKER.lorem().sentence());
    }

    List<CommentDTO> getAllCommentsAndExpectOkStatus(String jwtToken) {
        return client.get()
                .uri(COMMENTS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CommentDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

    void createCommentForStudentAndExpectOkStatus(
            String jwtToken,
            CreateCommentRequest createCommentRequest
    ) {
        client.post()
                .uri(COMMENTS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createCommentRequest), CreateCommentRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
