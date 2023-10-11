package com.example.schoolmanagementsystem.grade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Page<Grade> findByCourseStudentsId(Long studentId, Pageable pageable);
    Page<Grade> findByCourseTeacherId(Long teacherId, Pageable pageable);
}
