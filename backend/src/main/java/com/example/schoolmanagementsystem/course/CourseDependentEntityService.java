package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CourseDependentEntityService {
    public final CourseRepository courseRepository;
    public final AuthenticationUtil authenticationUtil;
    public final UpdateUtil updateUtil;

    public Course getValidCourseByIdOrElseThrowWithMessage(Long courseId, String errorMessage) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        User requestUser = authenticationUtil.getRequestUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(authenticationUtil.isUserAdmin() || course.getTeacher().equals(requestUser))) {
            throw new NotEnoughAuthorityException(
                    errorMessage

            );
        }

        return course;
    }
}
