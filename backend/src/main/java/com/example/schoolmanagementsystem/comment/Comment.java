package com.example.schoolmanagementsystem.comment;

import com.example.schoolmanagementsystem.course.Course;
import com.example.schoolmanagementsystem.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(nullable = false)
    private LocalDate datePublished;

    @ManyToOne
    private Course course;

    @ManyToOne
    private User student;

    @PrePersist
    public void prePersist() {
        this.datePublished = LocalDate.now();
    }
}
