package com.samreact.skooLLy.modules.grades.repository;

import com.samreact.skooLLy.modules.grades.entity.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {
    Optional<GradingScale> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    List<GradingScale> findAllBySchoolIdAndDeletedFalseOrderByMaxPercentageDesc(Long schoolId);

    Optional<GradingScale> findBySchoolIdAndGradeLetterAndDeletedFalse(
            Long schoolId, String gradeLetter);
}
