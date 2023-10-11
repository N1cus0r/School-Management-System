package com.example.schoolmanagementsystem.grade;

import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseDependentEntityService;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.user.UserRepository;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService extends CourseDependentEntityService {
    private final GradeRepository gradeRepository;
    private final GradeDTOMapper gradeDTOMapper;
    private final UserRepository userRepository;

    public GradeService(CourseRepository courseRepository, AuthenticationUtil authenticationUtil, UpdateUtil updateUtil, GradeRepository gradeRepository, GradeDTOMapper gradeDTOMapper, UserRepository userRepository) {
        super(courseRepository, authenticationUtil, updateUtil);
        this.gradeRepository = gradeRepository;
        this.gradeDTOMapper = gradeDTOMapper;
        this.userRepository = userRepository;
    }

    public List<GradeDTO> getAllGrades(int pageCount, int pageSize) {
        User requestUser = authenticationUtil.getRequestUser();

        Pageable pageable = PageRequest.of(pageCount, pageSize);
        Page<Grade> commentPage;

        if (requestUser.getRole().equals(Role.STUDENT)) {
            commentPage = gradeRepository.findByCourseStudentsId(
                    requestUser.getId(), pageable
            );
        } else if (requestUser.getRole().equals(Role.TEACHER)) {
            commentPage = gradeRepository.findByCourseTeacherId(
                    requestUser.getId(), pageable
            );
        } else {
            commentPage = gradeRepository.findAll(pageable);
        }

        return commentPage.getContent()
                .stream()
                .map(gradeDTOMapper)
                .collect(Collectors.toList());
    }

    public void addGradeForUser(CreateGradeRequest createGradeRequest) {
        Long courseId = createGradeRequest.courseId();
        Long studentId = createGradeRequest.studentId();

        Course course = getValidCourseByIdOrElseThrowWithMessage(
                courseId,
                "You don't have the right to add grade for this course"
        );

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id [%s] does not exist".formatted(studentId)));

        if (!courseRepository.existsByIdAndStudentsId(courseId, studentId)) {
            throw new RequestValidationError("Student does not belong to course");
        }

        gradeRepository.save(
                Grade.builder()
                        .value(createGradeRequest.value())
                        .text(createGradeRequest.text())
                        .course(course)
                        .student(student)
                        .build()
        );
    }

    public void updateGradeForUser(Long gradeId, UpdateGradeRequest updateGradeRequest) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grade with id [%s] does not exist".formatted(gradeId)));

        Course course = grade.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to update this grade");
        }

        boolean changes = false;

        if (!updateUtil.isFieldNullOrWithoutChange(grade.getValue(), updateGradeRequest.value())) {
            changes = true;
            grade.setValue(updateGradeRequest.value());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(grade.getText(), updateGradeRequest.text())) {
            changes = true;
            grade.setText(updateGradeRequest.text());
        }

        if (!changes) {
            throw new RequestValidationError("No data changes found");
        }

        gradeRepository.save(grade);
    }

    public void deleteGradeForUser(Long gradeId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grade with id [%s] does not exist".formatted(gradeId)));

        Course course = grade.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to delete this grade");
        }

        gradeRepository.delete(grade);
    }
}
