package com.example.schoolmanagementsystem.integration.course;

import com.example.schoolmanagementsystem.course.CreateCourseRequest;
import com.example.schoolmanagementsystem.course.UpdateCourseRequest;
import com.example.schoolmanagementsystem.integration.AbstractCourseRelatedIntegrationTest;

public abstract class AbstractCourseIntegrationTest extends AbstractCourseRelatedIntegrationTest {

    CreateCourseRequest getCreateCourseRequest(String prefix, Long teacherId) {
        return new CreateCourseRequest(
                prefix.isBlank() ?
                        FAKER.lorem().word() + " " + FAKER.lorem().word() :
                        prefix + FAKER.lorem().word() + " " + FAKER.lorem().word(),
                teacherId
        );
    }

    UpdateCourseRequest getUpdateCourseRequest() {
        return new UpdateCourseRequest(FAKER.lorem().word() + " " + FAKER.lorem().word());
    }
}
