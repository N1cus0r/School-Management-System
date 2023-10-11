package com.example.schoolmanagementsystem.attendance;

public record UpdateAttendanceRequest(
        AttendanceType type,
        AttendancePeriod period
) {}
