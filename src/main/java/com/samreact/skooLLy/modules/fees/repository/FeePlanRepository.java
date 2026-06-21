package com.samreact.skooLLy.modules.fees.repository;

import com.samreact.skooLLy.modules.fees.entity.FeePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeePlanRepository extends JpaRepository<FeePlan, Long> {
    Optional<FeePlan> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    Page<FeePlan> findAllBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    List<FeePlan> findAllBySchoolIdAndDeletedFalseAndActiveTrue(Long schoolId);

    List<FeePlan> findAllByClassroomIdAndTermIdAndSessionIdAndSchoolIdAndDeletedFalse(
            Long classroomId, Long termId, Long sessionId, Long schoolId);

    List<FeePlan> findAllByClassroomIdAndSchoolIdAndDeletedFalse(
            Long classroomId, Long schoolId);

    List<FeePlan> findAllByIdInAndSchoolIdAndDeletedFalse(
            List<Long> ids, Long schoolId);
}
