package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.StudentSubject;
import com.samreact.skooLLy.modules.academic.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    List<StudentSubject> findAllByStudentIdAndTermIdAndDeletedFalse(Long studentId, Long termId);

    List<StudentSubject> findAllByStudentIdAndTermIdAndStatusAndDeletedFalse(
            Long studentId, Long termId, EnrollmentStatus status);

    List<StudentSubject> findAllBySubjectIdAndTermIdAndDeletedFalse(Long subjectId, Long termId);

    List<StudentSubject> findAllBySchoolIdAndTermIdAndDeletedFalse(Long schoolId, Long termId);

    boolean existsByStudentIdAndSubjectIdAndTermIdAndDeletedFalse(
            Long studentId, Long subjectId, Long termId);

    Optional<StudentSubject> findByStudentIdAndSubjectIdAndTermIdAndDeletedFalse(
            Long studentId, Long subjectId, Long termId);

    List<StudentSubject> findAllByStudentIdAndDeletedFalse(Long studentId);

    long countBySchoolIdAndTermIdAndStatusAndDeletedFalse(
            Long schoolId, Long termId, EnrollmentStatus status);
}
