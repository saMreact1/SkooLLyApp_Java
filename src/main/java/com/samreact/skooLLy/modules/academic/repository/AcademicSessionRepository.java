package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.AcademicSession;
import com.samreact.skooLLy.modules.academic.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {
    List<AcademicSession> findAllBySchoolId(Long schoolId);

    Optional<AcademicSession> findByIdAndSchoolId(
            Long id, Long schoolId);

    Optional<AcademicSession> findBySchoolIdAndCurrentTrue(
            Long schoolId);

    boolean existsByNameAndSchoolId(
            String name, Long schoolId);

    List<AcademicSession> findAllBySchoolIdAndStatus(
            Long schoolId, SessionStatus status);

    Optional<AcademicSession> findFirstBySchoolIdAndStartDateAfterOrderByStartDateAsc(Long schoolId, LocalDate date);

    List<AcademicSession> findAllBySchoolIdOrderByStartDateAsc(Long schoolId);
}
