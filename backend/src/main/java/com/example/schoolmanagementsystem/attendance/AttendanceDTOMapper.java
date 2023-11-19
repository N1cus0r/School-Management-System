package com.example.schoolmanagementsystem.attendance;

import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class AttendanceDTOMapper implements Function<Attendance, AttendanceDTO> {
    @Override
    public AttendanceDTO apply(Attendance attendance) {
        return new AttendanceDTO(
                attendance.getId(),
                attendance.getType(),
                attendance.getPeriod(),
                attendance.getDatePublished(),
                attendance.getCourse().getName(),
                attendance.getCourse().getTeacher().getFullName(),
                attendance.getStudent().getFullName()
        );
    }
}
