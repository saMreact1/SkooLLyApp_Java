package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllBySchoolId(Long schoolId);

    Page<Subject> findAllBySchoolId(Long schoolId, Pageable pageable);

    Optional<Subject> findByIdAndSchoolId(Long id, Long schoolId);

    boolean existsByNameAndSchoolId(String name, Long schoolId);

    boolean existsByCodeAndSchoolId(String code, Long schoolId);

    List<Subject> findAllBySchoolIdAndIsElective(Long schoolId, boolean isElective);

    Page<Subject> findAllBySchoolIdAndIsElective(Long schoolId, boolean isElective, Pageable pageable);

    List<Subject> findAllBySchoolIdAndActive(Long schoolId, boolean active);

    Page<Subject> findAllBySchoolIdAndActive(Long schoolId, boolean active, Pageable pageable);
}