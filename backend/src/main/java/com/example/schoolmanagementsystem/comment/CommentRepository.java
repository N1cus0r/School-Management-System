package com.example.schoolmanagementsystem.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByCourseStudentsId(Long studentId, Pageable pageable);

    Page<Comment> findByCourseTeacherId(Long teacherId, Pageable pageable);

    Page<Comment> findByCourseId(Long courseId, Pageable pageable);
}
