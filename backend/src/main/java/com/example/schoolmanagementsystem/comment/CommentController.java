package com.example.schoolmanagementsystem.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping()
    public List<CommentDTO> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return commentService.getAllComments(page, size);
    }


    @PostMapping()
    public void addCommentForStudent(
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        commentService.addCommentForUser(createCommentRequest);
    }

    @PutMapping("{commentId}")
    public void updateAttendance(
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest
    ) {
        commentService.updateCommentForUser(commentId, updateCommentRequest);
    }

    @DeleteMapping("{commentId}")
    public void deleteComment(
            @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteCommentForUser(commentId);
    }
}
