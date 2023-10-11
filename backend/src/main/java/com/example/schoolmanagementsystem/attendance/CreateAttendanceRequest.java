package com.example.schoolmanagementsystem.attendance;

import jakarta.validation.constraints.NotNull;

public record CreateAttendanceRequest(
        @NotNull(message = "type name must not be empty")
        AttendanceType type,
        @NotNull(message = "period name must not be empty")
        AttendancePeriod period,
        @NotNull(message = "studentId name must not be empty")
        Long studentId,
        @NotNull(message = "courseId name must not be empty")
        Long courseId
) {}
