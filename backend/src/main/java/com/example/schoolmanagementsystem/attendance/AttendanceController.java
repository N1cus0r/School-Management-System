package com.example.schoolmanagementsystem.attendance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping()
    public List<AttendanceDTO> getAllAttendances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return attendanceService.getAllAttendances(page, size);
    }


    @PostMapping()
    public void addAttendanceForStudent(
            @Valid @RequestBody CreateAttendanceRequest createAttendanceRequest
    ) {
        attendanceService.addAttendanceForUser(createAttendanceRequest);
    }

    @PutMapping("{attendanceId}")
    public void updateAttendance(
            @PathVariable("attendanceId") Long attendanceId,
            @RequestBody UpdateAttendanceRequest updateAttendanceRequest
    ) {
        attendanceService.updateAttendanceForUser(attendanceId, updateAttendanceRequest);
    }

    @DeleteMapping("{attendanceId}")
    public void updateAttendance(
            @PathVariable("attendanceId") Long attendanceId
    ) {
        attendanceService.deleteAttendanceForUser(attendanceId);
    }
}
