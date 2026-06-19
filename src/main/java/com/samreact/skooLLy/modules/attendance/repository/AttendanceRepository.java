package com.samreact.skooLLy.modules.attendance.repository;

import com.samreact.skooLLy.modules.attendance.entity.Attendance;
import com.samreact.skooLLy.modules.attendance.entity.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    Optional<Attendance> findByStudentIdAndDateAndDeletedFalse(Long studentId, LocalDate date);

    Page<Attendance> findAllByStudentIdAndTermIdAndDeletedFalse(
            Long studentId, Long termId, Pageable pageable);

    List<Attendance> findAllByStudentIdAndTermIdAndDeletedFalse(
            Long studentId, Long termId);

    Page<Attendance> findAllByClassroomIdAndDateAndDeletedFalse(
            Long classroomId, LocalDate date, Pageable pageable);

    List<Attendance> findAllByClassroomIdAndDateAndDeletedFalse(
            Long classroomId, LocalDate date);

    List<Attendance> findAllByClassroomIdAndDateBetweenAndDeletedFalse(
            Long classroomId, LocalDate from, LocalDate to);

    List<Attendance> findAllByStudentIdAndDateBetweenAndDeletedFalse(
            Long studentId, LocalDate from, LocalDate to);

    long countByStudentIdAndTermIdAndStatusAndDeletedFalse(
            Long studentId, Long termId, AttendanceStatus status);

    long countByStudentIdAndTermIdAndDeletedFalse(
            Long studentId, Long termId);

    List<Attendance> findAllByClassroomIdAndTermIdAndDeletedFalse(
            Long classroomId, Long termId);
}
