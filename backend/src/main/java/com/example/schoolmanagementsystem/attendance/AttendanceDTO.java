package com.example.schoolmanagementsystem.attendance;

import java.time.LocalDate;

public record AttendanceDTO (
    Long id,
    AttendanceType type,
    AttendancePeriod period,
    LocalDate datePublished,
    String courseName,
    String courseTeacher,
    String studentName
) {}
