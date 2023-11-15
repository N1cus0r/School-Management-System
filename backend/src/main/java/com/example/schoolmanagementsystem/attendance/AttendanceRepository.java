package com.example.schoolmanagementsystem.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Page<Attendance> findByCourseStudentsId(Long studentId, Pageable pageable);
    Page<Attendance> findByCourseTeacherId(Long teacherId, Pageable pageable);
    Page<Attendance> findByCourseId(Long courseId, Pageable pageable);
}


