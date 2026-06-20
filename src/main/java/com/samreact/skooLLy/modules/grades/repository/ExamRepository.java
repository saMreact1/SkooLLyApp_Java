package com.samreact.skooLLy.modules.grades.repository;

import com.samreact.skooLLy.modules.grades.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    Page<Exam> findAllBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    Page<Exam> findAllByClassroomIdAndSchoolIdAndDeletedFalse(
            Long classroomId, Long schoolId, Pageable pageable);

    Page<Exam> findAllBySubjectIdAndSchoolIdAndDeletedFalse(
            Long subjectId, Long schoolId, Pageable pageable);

    Page<Exam> findAllByTermIdAndSchoolIdAndDeletedFalse(
            Long termId, Long schoolId, Pageable pageable);

    List<Exam> findAllByClassroomIdAndSubjectIdAndTermIdAndSchoolIdAndDeletedFalse(
            Long classroomId, Long subjectId, Long termId, Long schoolId);

    List<Exam> findAllByTermIdAndSchoolIdAndDeletedFalse(
            Long termId, Long schoolId);

    @Query("SELECT COUNT(e) FROM Grade g JOIN g.exam e WHERE e.id = :examId AND g.deleted = false")
    long countGradesByExamId(@Param("examId") Long examId);
}
