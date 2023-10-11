package com.example.schoolmanagementsystem.homework;

import com.example.schoolmanagementsystem.auth.AuthenticationUtil;
import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.course.CourseDependentEntityService;
import com.example.schoolmanagementsystem.course.CourseRepository;
import com.example.schoolmanagementsystem.exception.NotEnoughAuthorityException;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.example.schoolmanagementsystem.exception.ResourceNotFoundException;
import com.example.schoolmanagementsystem.user.Role;
import com.example.schoolmanagementsystem.user.User;
import com.example.schoolmanagementsystem.util.UpdateUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class HomeworkService extends CourseDependentEntityService {
    private final HomeworkRepository homeworkRepository;
    private final HomeworkDTOMapper homeworkDTOMapper;
    public HomeworkService(CourseRepository courseRepository, AuthenticationUtil authenticationUtil, UpdateUtil updateUtil, HomeworkRepository homeworkRepository, HomeworkDTOMapper homeworkDTOMapper) {
        super(courseRepository, authenticationUtil, updateUtil);
        this.homeworkRepository = homeworkRepository;
        this.homeworkDTOMapper = homeworkDTOMapper;
    }

    public List<HomeworkDTO> getAllHomeworks(int pageCount, int pageSize) {
        User requestUser = authenticationUtil.getRequestUser();

        Pageable pageable = PageRequest.of(pageCount, pageSize);
        Page<Homework> homerowkPage;

        if (requestUser.getRole().equals(Role.STUDENT)) {
            homerowkPage = homeworkRepository.findByCourseStudentsId(
                    requestUser.getId(), pageable
            );
        } else if (requestUser.getRole().equals(Role.TEACHER)) {
            homerowkPage = homeworkRepository.findByCourseTeacherId(
                    requestUser.getId(), pageable
            );
        } else {
            homerowkPage = homeworkRepository.findAll(pageable);
        }

        return homerowkPage.getContent()
                .stream()
                .map(homeworkDTOMapper)
                .collect(Collectors.toList());
    }

    public void addHomeworkForCourse(CreateHomeworkRequest homeworkRequest) {
        Course course = getValidCourseByIdOrElseThrowWithMessage(
                homeworkRequest.courseId(),
                "You don't have the right to add homeworks for this course"
        );

        homeworkRepository.save(
                Homework.builder()
                        .text(homeworkRequest.text())
                        .dueDate(homeworkRequest.dueDate())
                        .course(course)
                        .build()
        );
    }

    public void updateHomeworkForCourse(
            Long homeworkId,
            UpdateHomeworkRequest updateHomeworkRequest
    ) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Homework with id [%s] does not exist".formatted(homeworkId)));

        Course course = homework.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to update this homework");
        }

        boolean changes = false;

        if (!updateUtil.isFieldNullOrWithoutChange(homework.getText(), updateHomeworkRequest.text())) {
            changes = true;
            homework.setText(updateHomeworkRequest.text());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(homework.getDueDate(), updateHomeworkRequest.dueDate())) {
            changes = true;
            homework.setDueDate(updateHomeworkRequest.dueDate());
        }

        if (!changes) {
            throw new RequestValidationError("No data changes found");
        }

        homeworkRepository.save(homework);
    }

    public void deleteHomeworkForCourse(Long homeworkId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Homework with id [%s] does not exist".formatted(homeworkId)));

        Course course = homework.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to update this homework");
        }

        homeworkRepository.delete(homework);
    }
}
