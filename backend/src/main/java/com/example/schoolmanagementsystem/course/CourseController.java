package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.user.UserDTO;
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

    @GetMapping("{courseId}")
    public CourseDTO getCourseById(@PathVariable("courseId") Long courseId) {
        return courseService.getCourseById(courseId);
    }

    @GetMapping("{courseId}/homeworks")
    public List<HomeworkDTO> getCourseHomeworks(
            @PathVariable("courseId") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getCourseHomeworks(courseId, page, size);
    }

    @GetMapping("{courseId}/grades")
    public List<GradeDTO> getCourseGrades(
            @PathVariable("courseId") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getCourseGrades(courseId, page, size);
    }

    @GetMapping("{courseId}/attendances")
    public List<AttendanceDTO> getCourseAttendances(
            @PathVariable("courseId") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getCourseAttendances(courseId, page, size);
    }

    @GetMapping("{courseId}/comments")
    public List<CommentDTO> getCourseComments(
            @PathVariable("courseId") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getCourseComments(courseId, page, size);
    }

    @GetMapping("{courseId}/students")
    public List<UserDTO> getCourseStudents(
            @PathVariable("courseId") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getCourseStudents(courseId, page, size);
    }

    @GetMapping("{courseId}/non-participating-students")
    public List<UserDTO> getCourseNonParticipatingStudents(
            @PathVariable("courseId") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return courseService.getCourseNonParticipatingStudents(courseId, page, size);
    }

}
