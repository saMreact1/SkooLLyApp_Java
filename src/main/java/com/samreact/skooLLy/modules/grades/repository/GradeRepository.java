package com.samreact.skooLLy.modules.grades.repository;

import com.samreact.skooLLy.modules.grades.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    Optional<Grade> findByExamIdAndStudentIdAndDeletedFalse(
            Long examId, Long studentId);

    List<Grade> findAllByExamIdAndDeletedFalse(Long examId);

    List<Grade> findAllByStudentIdAndDeletedFalse(Long studentId);

    List<Grade> findAllByExamIdAndStudentIdInAndDeletedFalse(
            Long examId, List<Long> studentIds);

    long countByExamIdAndDeletedFalse(Long examId);

    @Query("SELECT g.marksObtained FROM Grade g WHERE g.exam.id = :examId AND g.deleted = false")
    List<java.math.BigDecimal> findMarksByExamId(@Param("examId") Long examId);

    @Query("SELECT AVG(g.marksObtained) FROM Grade g WHERE g.exam.id = :examId AND g.deleted = false")
    java.math.BigDecimal findAverageByExamId(@Param("examId") Long examId);

    @Query("SELECT MAX(g.marksObtained) FROM Grade g WHERE g.exam.id = :examId AND g.deleted = false")
    java.math.BigDecimal findMaxByExamId(@Param("examId") Long examId);

    @Query("SELECT MIN(g.marksObtained) FROM Grade g WHERE g.exam.id = :examId AND g.deleted = false")
    java.math.BigDecimal findMinByExamId(@Param("examId") Long examId);
}
