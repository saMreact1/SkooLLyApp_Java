package com.samreact.skooLLy.modules.student.repository;

import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllBySchoolIdAndDeleted(Long schoolId, boolean deleted);
    Page<Student> findAllBySchoolIdAndDeleted(Long schoolId, boolean deleted, Pageable pageable);
    Optional<Student> findByIdAndSchoolIdAndDeleted(Long id, Long schoolId, boolean deleted);
    Optional<Student> findByAdmissionNumberAndSchoolIdAndDeleted(String admissionNumber, Long schoolId, boolean deleted);
    Optional<Student> findByUserIdAndDeleted(Long userId, boolean deleted);
    List<Student> findAllBySchoolIdAndCurrentClassAndDeleted(Long schoolId, String currentClass, boolean deleted);
    Page<Student> findAllBySchoolIdAndCurrentClassAndDeleted(Long schoolId, String currentClass, boolean deleted, Pageable pageable);
    List<Student> findAllBySchoolIdAndStatusAndDeleted(Long schoolId, StudentStatus status, boolean deleted);
    Page<Student> findAllBySchoolIdAndStatusAndDeleted(Long schoolId, StudentStatus status, boolean deleted, Pageable pageable);
    boolean existsByAdmissionNumberAndSchoolIdAndDeleted(String admissionNumber, Long schoolId, boolean deleted);
    long countBySchoolIdAndDeleted(Long schoolId, boolean deleted);
}
