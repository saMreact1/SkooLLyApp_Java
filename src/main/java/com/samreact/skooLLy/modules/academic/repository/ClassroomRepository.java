package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findAllBySchoolId(Long schoolId);

    Page<Classroom> findAllBySchoolId(Long schoolId, Pageable pageable);

    Optional<Classroom> findByIdAndSchoolId(Long id, Long schoolId);

    boolean existsByNameAndSectionAndSchoolId(String name, String section, Long schoolId);

    List<Classroom> findAllBySchoolIdAndLevel(Long schoolId, String level);

    Page<Classroom> findAllBySchoolIdAndLevel(Long schoolId, String level, Pageable pageable);

    List<Classroom> findAllBySchoolIdAndActive(Long schoolId, boolean active);

    Page<Classroom> findAllBySchoolIdAndActive(Long schoolId, boolean active, Pageable pageable);

    long countBySchoolId(Long schoolId);
}