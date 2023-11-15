package com.example.schoolmanagementsystem.homework;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    Page<Homework> findByCourseStudentsId(Long studentId, Pageable pageable);
    Page<Homework> findByCourseTeacherId(Long teacherId, Pageable pageable);
    Page<Homework> findByCourseId(Long courseId, Pageable pageable);
}
