package com.example.schoolmanagementsystem.attendance;

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
public class AttendanceService extends CourseDependentEntityService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceDTOMapper attendanceDTOMapper;
    private final UserRepository userRepository;

    public AttendanceService(CourseRepository courseRepository, AuthenticationUtil authenticationUtil, UpdateUtil updateUtil, AttendanceRepository attendanceRepository, AttendanceDTOMapper attendanceDTOMapper, UserRepository userRepository) {
        super(courseRepository, authenticationUtil, updateUtil);
        this.attendanceRepository = attendanceRepository;
        this.attendanceDTOMapper = attendanceDTOMapper;
        this.userRepository = userRepository;
    }

    public List<AttendanceDTO> getAllAttendances(int pageCount, int pageSize) {
        User requestUser = authenticationUtil.getRequestUser();

        Pageable pageable = PageRequest.of(pageCount, pageSize);
        Page<Attendance> attendancePage;

        if (requestUser.getRole().equals(Role.STUDENT)) {
            attendancePage = attendanceRepository.findByCourseStudentsId(
                    requestUser.getId(), pageable
            );
        } else if (requestUser.getRole().equals(Role.TEACHER)) {
            attendancePage = attendanceRepository.findByCourseTeacherId(
                    requestUser.getId(), pageable
            );
        } else {
            attendancePage = attendanceRepository.findAll(pageable);
        }

        return attendancePage.getContent()
                .stream()
                .map(attendanceDTOMapper)
                .collect(Collectors.toList());
    }

    public void addAttendanceForUser(
            CreateAttendanceRequest createAttendanceRequest
    ) {
        Long courseId = createAttendanceRequest.courseId();
        Long studentId = createAttendanceRequest.studentId();

        Course course = getValidCourseByIdOrElseThrowWithMessage(
                courseId,
                "You don't have the right to add attendance for this course"
        );

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id [%s] does not exist".formatted(studentId)));

        if (!courseRepository.existsByIdAndStudentsId(courseId, studentId)) {
            throw new RequestValidationError("Student does not belong to course");
        }

        attendanceRepository.save(
                Attendance.builder()
                        .type(createAttendanceRequest.type())
                        .period(createAttendanceRequest.period())
                        .course(course)
                        .student(student)
                        .build()
        );
    }

    public void updateAttendanceForUser(
            Long attendanceId,
            UpdateAttendanceRequest updateAttendanceRequest
    ) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance with id [%s] does not exist".formatted(attendanceId)));

        Course course = attendance.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to update this attendance");
        }

        boolean changes = false;

        if (!updateUtil.isFieldNullOrWithoutChange(attendance.getType(), updateAttendanceRequest.type())) {
            changes = true;
            attendance.setType(updateAttendanceRequest.type());
        }

        if (!updateUtil.isFieldNullOrWithoutChange(attendance.getPeriod(), updateAttendanceRequest.period())) {
            changes = true;
            attendance.setPeriod(updateAttendanceRequest.period());
        }

        if (!changes) {
            throw new RequestValidationError("No data changes found");
        }

        attendanceRepository.save(attendance);
    }

    public void deleteAttendanceForUser(Long attendanceId) {
        if (authenticationUtil.isUserStudent()) {
            throw new NotEnoughAuthorityException("You don't have the right use this service");
        }

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance with id [%s] does not exist".formatted(attendanceId)));

        Course course = attendance.getCourse();

        if (!(course.getTeacher().equals(authenticationUtil.getRequestUser()) || authenticationUtil.isUserAdmin())) {
            throw new NotEnoughAuthorityException("You don't have the right to delete this attendance");
        }

        attendanceRepository.delete(attendance);
    }

    public List<AttendanceDTO> getByCourseId(Long courseId, Pageable pageable) {
        return attendanceRepository.findByCourseId(courseId, pageable).getContent()
                .stream()
                .map(attendanceDTOMapper)
                .collect(Collectors.toList());
    }
}
