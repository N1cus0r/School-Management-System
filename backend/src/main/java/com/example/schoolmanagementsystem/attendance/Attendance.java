package com.example.schoolmanagementsystem.attendance;

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
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendancePeriod period;

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
