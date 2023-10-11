package com.example.schoolmanagementsystem.integration.attendance;

import com.example.schoolmanagementsystem.attendance.*;
import com.example.schoolmanagementsystem.integration.AbstractCourseRelatedIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class AbstractAttendanceIntegrationTest extends AbstractCourseRelatedIntegrationTest {
    static final String ATTENDANCES_URI = "/api/v1/attendances";

    CreateAttendanceRequest getCreateAttendanceRequest(
            Long studentId,
            Long courseId
    ) {
        return new CreateAttendanceRequest(
                AttendanceType.LATE,
                AttendancePeriod.LESSON_1,
                studentId,
                courseId
        );
    }

    UpdateAttendanceRequest getUpdateAttendanceRequest() {
        return new UpdateAttendanceRequest(
                AttendanceType.ABSENT,
                AttendancePeriod.LESSON_5
        );
    }

    List<AttendanceDTO> getAllAttendancesAndExpectOkStatus(String jwtToken) {
        return client.get()
                .uri(ATTENDANCES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<AttendanceDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

    void createAttendanceForStudentAndExpectOkStatus(
            String jwtToken,
            CreateAttendanceRequest createAttendanceRequest
    ) {
        client.post()
                .uri(ATTENDANCES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(createAttendanceRequest), CreateAttendanceRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
