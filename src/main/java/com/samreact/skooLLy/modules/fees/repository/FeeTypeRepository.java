package com.samreact.skooLLy.modules.fees.repository;

import com.samreact.skooLLy.modules.fees.entity.FeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
    Optional<FeeType> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    Page<FeeType> findAllBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    List<FeeType> findAllBySchoolIdAndDeletedFalseAndActiveTrue(Long schoolId);

    boolean existsByNameAndSchoolIdAndDeletedFalse(String name, Long schoolId);
}
