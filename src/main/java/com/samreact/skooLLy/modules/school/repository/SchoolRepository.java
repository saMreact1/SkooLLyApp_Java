package com.samreact.skooLLy.modules.school.repository;

import com.samreact.skooLLy.modules.school.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);

    Optional<School> findBySchoolCode(String schoolCode);

    Optional<School> findByNameAndDeleted(String name, boolean deleted);

    Optional<School> findBySchoolCodeAndDeleted(String schoolCode, boolean deleted);

    boolean existsByEmail(String email);

    boolean existsByName(String name);
}
