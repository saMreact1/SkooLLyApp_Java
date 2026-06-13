package com.samreact.skooLLy.modules.teacher.repository;

import com.samreact.skooLLy.modules.teacher.entity.Teacher;
import com.samreact.skooLLy.modules.teacher.entity.enums.TeacherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllBySchoolIdAndDeleted(Long schoolId, boolean deleted);

    Optional<Teacher> findByIdAndSchoolId(Long id, Long schoolId);

    Optional<Teacher> findByIdAndSchoolIdAndDeleted(Long id, Long schoolId, boolean deleted);

    Optional<Teacher> findByStaffIdAndSchoolIdAndDeleted(String staffId, Long schoolId, boolean deleted);

    Optional<Teacher> findByUserIdAndDeleted(Long userId, boolean deleted);

    List<Teacher> findAllBySchoolIdAndStatusAndDeleted(Long schoolId, TeacherStatus status, boolean deleted);

    boolean existsByStaffIdAndSchoolIdAndDeleted(String staffId, Long schoolId, boolean deleted);

    boolean existsByUserIdAndDeleted(Long userId, boolean deleted);

    long countBySchoolIdAndDeleted(Long schoolId, boolean deleted);
}
