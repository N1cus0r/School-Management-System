package com.example.schoolmanagementsystem.integration;

import com.example.schoolmanagementsystem.course.CourseDTO;
import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class AbstractCourseRelatedIntegrationTest extends AbstractIntegrationTest {
    public final String COURSES_URI = "/api/v1/courses";

    public void createCourseForTeacherAndExpectOkStatus(
            String jwtToken,
            CreateCourseRequest courseRequest
    ) {
        client.post()
                .uri(COURSES_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .body(Mono.just(courseRequest), CreateCourseRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    public CreateCourseRequest getCreateCourseRequest(Long teacherId) {
        return new CreateCourseRequest(FAKER.lorem().word() + " " + FAKER.lorem().word(), teacherId);
    }

    public CourseDTO getCourseByName(String jwtToken, String name) {
        return client.get()
                .uri(COURSES_URI + "/byName/" + name)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CourseDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }

    public List<CourseDTO> getCoursesSearchByNameAndExpectOkStatus(String jwtToken, String search) {
        return client.get()
                .uri(COURSES_URI + "?nameSearch=" + search)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CourseDTO>() {
                })
                .returnResult()
                .getResponseBody();
    }
    public Long getCourseIdByNameFromResultList(
            String name,
            List<CourseDTO> courses
    ) {
        return courses.stream()
                .filter(c -> c.name().equals(name))
                .map(CourseDTO::id)
                .findFirst()
                .orElseThrow();
    }

    public void addStudentToCourseAndExpectOkStatus(String jwtToken, Long courseId, Long studentId) {
        client.put()
                .uri(COURSES_URI + "/%s/add-student/%s".formatted(courseId, studentId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }
}
