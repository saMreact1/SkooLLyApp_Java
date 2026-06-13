package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findAllBySchoolId(Long schoolId);

    Optional<Classroom> findByIdAndSchoolId(Long id, Long schoolId);

    boolean existsByNameAndSectionAndSchoolId(String name, String section, Long schoolId);

    List<Classroom> findAllBySchoolIdAndLevel(Long schoolId, String level);

    List<Classroom> findAllBySchoolIdAndActive(Long schoolId, boolean active);

    long countBySchoolId(Long schoolId);
}