package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
import com.example.schoolmanagementsystem.attendance.AttendanceService;
import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.comment.CommentDTO;
import com.example.schoolmanagementsystem.comment.CommentService;
import com.example.schoolmanagementsystem.exception.CourseNameTakenException;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.grade.GradeDTO;
import com.example.schoolmanagementsystem.grade.GradeService;
import com.example.schoolmanagementsystem.homework.HomeworkDTO;
import com.example.schoolmanagementsystem.homework.HomeworkRepository;
import com.example.schoolmanagementsystem.homework.HomeworkService;
import com.example.schoolmanagementsystem.user.*;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final HomeworkService homeworkService;
    private final GradeService gradeService;
    private final AttendanceService attendanceService;
    private final CommentService commentService;
    private final UserService userService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseDTOMapper courseDTOMapper;
    private final AuthenticationUtil authenticationUtil;
    private final UpdateUtil updateUtil;

    private List<CourseDTO> getCoursesSearcherByName(
            String nameSearch,
            int pageCount,
            int pageSize
    ) {

        Pageable pageable = PageRequest.of(pageCount, pageSize);
        Page<Course> coursePage;

        User user = authenticationUtil.getRequestUser();

        if (nameSearch.isEmpty()) {
            coursePage =
                    user.getRole().equals(Role.ADMIN) ?
                            courseRepository
                                    .findAll(pageable)
                            : courseRepository
                            .findByTeacherId(user.getId(), pageable);
            ;
        } else {
            coursePage =
                    user.getRole().equals(Role.ADMIN) ?
                            courseRepository
                                    .findByNameStartsWithIgnoreCase(nameSearch, pageable)
                            : courseRepository
                            .findByTeacherIdAndNameStartsWithIgnoreCase(user.getId(), nameSearch, pageable);
            ;
        }

        return coursePage.getContent()
                .stream()
                .map(courseDTOMapper)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseByName(String name) {
        if (!authenticationUtil.isUserAdmin()) {
            throw new NotEnoughAuthorityException("You don't have the right to access this resource");
        }

        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with name [%s] does not exist".formatted(name)));

        return courseDTOMapper.apply(course);
    }

    public List<CourseDTO> getAllCourses(
            String nameSearch,
            int pageCount,
            int pageSize
    ) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right to access this information");
        }

        return getCoursesSearcherByName(nameSearch, pageCount, pageSize);
    }

    private User getValidStudentByIdOrElseThrow(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id [%s] does not exist".formatted(studentId)));

        if (!student.getRole().equals(Role.STUDENT)) {
            throw new RequestValidationError("You can't add courses to user with id [%s]".formatted(studentId));
        }

        return student;
    }

    public void addCourseForTeacher(CreateCourseRequest createCourseRequest) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Long teacherId = createCourseRequest.teacherId();

        if (!(authenticationUtil.getRequestUser().getId().equals(teacherId) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to create courses for other teachers");
        }

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id [%s] does not exist".formatted(teacherId)));


        courseRepository.save(
                Course.builder()
                        .name(createCourseRequest.name())
                        .teacher(teacher)
                        .build()
        );
    }

    public void updateTeacherCourse(Long courseId, UpdateCourseRequest updateCourseRequest) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to update this course");
        }

        boolean changes = false;

        if (!updateUtil.isFieldNullOrWithoutChange(course.getName(), updateCourseRequest.name())) {
            if (courseRepository.existsByName(updateCourseRequest.name())) {
                throw new CourseNameTakenException(
                        "Provided name is already in use"
                );
            }

            changes = true;
            course.setName(updateCourseRequest.name());
        }

        if (!changes) {
            throw new RequestValidationError("No data changes found");
        }

        courseRepository.save(course);
    }

    public void deleteUserCourse(Long courseId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to delete this course");
        }

        courseRepository.delete(course);
    }

    public void addStudentToCourse(Long courseId, Long studentId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to add students to this course");
        }

        User student = getValidStudentByIdOrElseThrow(studentId);

        student.getCourses().add(course);

        userRepository.save(student);
    }


    public void removeStudentFromCourse(Long courseId, Long studentId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to remove students from this course");
        }

        User student = getValidStudentByIdOrElseThrow(studentId);

        student.getCourses().remove(course);

        userRepository.save(student);
    }

    public CourseDTO getCourseById(Long courseId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to access this information");
        }

        return courseDTOMapper.apply(course);
    }

    private void validateCourseByIdAndOwner(Long courseId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course with id [%s] does not exist".formatted(courseId)));

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to access this information");
        }
    }

    public List<HomeworkDTO> getCourseHomeworks(Long courseId, int pageCount, int pageSize) {
        validateCourseByIdAndOwner(courseId);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        return homeworkService.getByCourseId(courseId, pageable);
    }

    public List<GradeDTO> getCourseGrades(Long courseId, int pageCount, int pageSize) {
        validateCourseByIdAndOwner(courseId);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        return gradeService.getByCourseId(courseId, pageable);
    }

    public List<AttendanceDTO> getCourseAttendances(Long courseId, int pageCount, int pageSize) {
        validateCourseByIdAndOwner(courseId);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        return attendanceService.getByCourseId(courseId, pageable);
    }

    public List<CommentDTO> getCourseComments(Long courseId, int pageCount, int pageSize) {
        validateCourseByIdAndOwner(courseId);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        return commentService.getByCourseId(courseId, pageable);
    }

    public List<UserDTO> getCourseStudents(Long courseId, int pageCount, int pageSize) {
        validateCourseByIdAndOwner(courseId);

        Pageable pageable = PageRequest.of(pageCount, pageSize);

        return userService.getByCourseId(courseId, pageable);
    }
}
