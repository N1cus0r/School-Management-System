package com.example.schoolmanagementsystem.grade;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;

    @GetMapping()
    public List<GradeDTO> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return gradeService.getAllGrades(page, size);
    }


    @PostMapping()
    public void addCommentForStudent(
            @Valid @RequestBody CreateGradeRequest createGradeRequest
    ) {
        gradeService.addGradeForUser(createGradeRequest);
    }

    @PutMapping("{gradeId}")
    public void updateAttendance(
            @PathVariable("gradeId") Long gradeId,
            @Valid @RequestBody UpdateGradeRequest updateGradeRequest
    ) {
        gradeService.updateGradeForUser(gradeId, updateGradeRequest);
    }

    @DeleteMapping("{gradeId}")
    public void deleteComment(
            @PathVariable("gradeId") Long gradeId
    ) {
        gradeService.deleteGradeForUser(gradeId);
    }

}
