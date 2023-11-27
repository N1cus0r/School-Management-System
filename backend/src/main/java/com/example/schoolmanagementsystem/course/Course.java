package com.example.schoolmanagementsystem.course;

import com.example.schoolmanagementsystem.attendance.Attendance;
import com.example.schoolmanagementsystem.comment.Comment;
import com.example.schoolmanagementsystem.grade.Grade;
import com.example.schoolmanagementsystem.homework.Homework;
import com.example.schoolmanagementsystem.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "course",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "course_name_unique",
                        columnNames = "name"
                ),
        }
)
public class Course {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToOne
    private User teacher;

    @ManyToMany
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<User> students = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Attendance> attendances = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Grade> grades = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Homework> homeworks = new HashSet<>();
    @PrePersist
    public void prePersist() {
        this.students = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
