package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllBySchoolId(Long schoolId);

    Optional<Subject> findByIdAndSchoolId(Long id, Long schoolId);

    boolean existsByNameAndSchoolId(String name, Long schoolId);

    boolean existsByCodeAndSchoolId(String code, Long schoolId);

    List<Subject> findAllBySchoolIdAndIsElective(Long schoolId, boolean isElective);

    List<Subject> findAllBySchoolIdAndActive(Long schoolId, boolean active);
}