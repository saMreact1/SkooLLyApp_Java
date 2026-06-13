package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.entity.enums.TermStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findAllBySessionId(Long sessionId);

    Optional<Term> findByIdAndSchoolId(Long id, Long schoolId);

    Optional<Term> findBySchoolIdAndCurrentTrue(Long schoolId);

    List<Term> findAllBySchoolIdAndStatus(Long schoolId, TermStatus status);

    boolean existsByNameAndSessionId(String name, Long sessionId);
}