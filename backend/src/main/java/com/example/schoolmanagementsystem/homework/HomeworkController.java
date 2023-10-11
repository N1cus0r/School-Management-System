package com.example.schoolmanagementsystem.homework;

import com.example.schoolmanagementsystem.attendance.CreateAttendanceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/homeworks")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    @GetMapping
    public List<HomeworkDTO> getAllHomeworks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return homeworkService.getAllHomeworks(page, size);
    }

    @PostMapping()
    public void addHomeworkForCourse(
            @Valid @RequestBody CreateHomeworkRequest homeworkRequest
    ) {
        homeworkService.addHomeworkForCourse(homeworkRequest);
    }

    @PutMapping("{homeworkId}")
    public void updateHomeworkForCourse(
            @PathVariable("homeworkId") Long homeworkId,
            @Valid @RequestBody UpdateHomeworkRequest updateHomeworkRequest
    ) {
        homeworkService.updateHomeworkForCourse(homeworkId, updateHomeworkRequest);
    }

    @DeleteMapping("{homeworkId}")
    public void updateHomeworkForCourse(
            @PathVariable("homeworkId") Long homeworkId
    ) {
        homeworkService.deleteHomeworkForCourse(homeworkId);
    }
}
