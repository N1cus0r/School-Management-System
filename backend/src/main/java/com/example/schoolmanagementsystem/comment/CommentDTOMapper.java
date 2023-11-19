package com.example.schoolmanagementsystem.comment;

import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class CommentDTOMapper implements Function<Comment, CommentDTO> {
    @Override
    public CommentDTO apply(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getDatePublished(),
                comment.getCourse().getName(),
                comment.getCourse().getTeacher().getFullName(),
                comment.getStudent().getFullName()
        );
    }
}
