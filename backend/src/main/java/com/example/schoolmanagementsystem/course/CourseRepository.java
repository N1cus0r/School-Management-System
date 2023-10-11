package com.example.schoolmanagementsystem.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByName(String name);
    Page<Course> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);
    Page<Course> findByTeacherIdAndNameStartsWithIgnoreCase(Long teacherId, String name, Pageable pageable);
    boolean existsByIdAndStudentsId(Long courseId, Long studentId);
}
