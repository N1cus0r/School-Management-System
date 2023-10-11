package com.example.schoolmanagementsystem.homework;

import com.example.schoolmanagementsystem.course.Course;
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
public class Homework {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDate datePublished;

    @Column(nullable = false)
    private LocalDate dueDate;

    @ManyToOne
    private Course course;

    @PrePersist
    public void prePersist() {
        this.datePublished = LocalDate.now();
    }
}
