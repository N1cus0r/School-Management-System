package com.example.schoolmanagementsystem.comment;

import com.example.schoolmanagementsystem.attendance.Attendance;
import com.example.schoolmanagementsystem.attendance.AttendanceDTO;
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
public class CommentService extends CourseDependentEntityService {
    private final CommentRepository commentRepository;
    private final CommentDTOMapper commentDTOMapper;
    private final UserRepository userRepository;

    public CommentService(CourseRepository courseRepository, AuthenticationUtil authenticationUtil, UpdateUtil updateUtil, CommentRepository commentRepository, CommentDTOMapper commentDTOMapper, UserRepository userRepository) {
        super(courseRepository, authenticationUtil, updateUtil);
        this.commentRepository = commentRepository;
        this.commentDTOMapper = commentDTOMapper;
        this.userRepository = userRepository;
    }


    public List<CommentDTO> getAllComments(int pageCount, int pageSize) {
        User requestUser = authenticationUtil.getRequestUser();

        Pageable pageable = PageRequest.of(pageCount, pageSize);
        Page<Comment> commentPage;

        if (requestUser.getRole().equals(Role.STUDENT)) {
            commentPage = commentRepository.findByCourseStudentsId(
                    requestUser.getId(), pageable
            );
        } else if (requestUser.getRole().equals(Role.TEACHER)) {
            commentPage = commentRepository.findByCourseTeacherId(
                    requestUser.getId(), pageable
            );
        } else {
            commentPage = commentRepository.findAll(pageable);
        }

        return commentPage.getContent()
                .stream()
                .map(commentDTOMapper)
                .collect(Collectors.toList());
    }

    public void addCommentForUser(CreateCommentRequest createCommentRequest) {
        Long courseId = createCommentRequest.courseId();
        Long studentId = createCommentRequest.studentId();

        Course course = getValidCourseByIdOrElseThrowWithMessage(
                courseId,
                "You don't have the right to add comment for this course"
        );

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id [%s] does not exist".formatted(studentId)));

        if (!courseRepository.existsByIdAndStudentsId(courseId, studentId)) {
            throw new RequestValidationError("Student does not belong to course");
        }

        commentRepository.save(
                Comment.builder()
                        .text(createCommentRequest.text())
                        .course(course)
                        .student(student)
                        .build()
        );
    }

    public void updateCommentForUser(Long commentId, UpdateCommentRequest updateCommentRequest) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment with id [%s] does not exist".formatted(commentId)));

        Course course = comment.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to update this comment");
        }

        boolean changes = false;

        if (!updateUtil.isFieldNullOrWithoutChange(comment.getText(), updateCommentRequest.text())) {
            changes = true;
            comment.setText(updateCommentRequest.text());
        }

        if (!changes) {
            throw new RequestValidationError("No data changes found");
        }

        commentRepository.save(comment);
    }

    public void deleteCommentForUser(Long commentId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment with id [%s] does not exist".formatted(commentId)));

        Course course = comment.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
