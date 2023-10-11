package com.example.schoolmanagementsystem.course;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    @GetMapping("byName/{name}")
    public CourseDTO getCourseByName(
            @PathVariable("name") String name
    ) {
        return courseService.getCourseByName(name);
    }

    @GetMapping()
    public List<CourseDTO> searchCoursesByName(
            @RequestParam(defaultValue = "") String nameSearch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getAllCourses(nameSearch, page, size);
    }

    @PostMapping()
    public void addCourseForTeacher(
            @Valid @RequestBody CreateCourseRequest createCourseRequest
    ) {
        courseService.addCourseForTeacher(createCourseRequest);
    }

    @PutMapping("{courseId}")
    public void updateTeacherCourse(
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody UpdateCourseRequest updateCourseRequest
    ) {
        courseService.updateTeacherCourse(courseId, updateCourseRequest);
    }

    @DeleteMapping("{courseId}")
    public void updateTeacherCourse(
            @PathVariable("courseId") Long courseId
    ) {
        courseService.deleteUserCourse(courseId);
    }

    @PutMapping("{courseId}/add-student/{studentId}")
    public void addStudentToCourse(
            @PathVariable("courseId") Long courseId,
            @PathVariable("studentId") Long studentId
    ) {
        courseService.addStudentToCourse(courseId, studentId);
    }

    @DeleteMapping("{courseId}/remove-student/{studentId}")
    public void removeStudentFromCourse(
            @PathVariable("courseId") Long courseId,
            @PathVariable("studentId") Long studentId
    ) {
        courseService.removeStudentFromCourse(courseId, studentId);
    }
}
