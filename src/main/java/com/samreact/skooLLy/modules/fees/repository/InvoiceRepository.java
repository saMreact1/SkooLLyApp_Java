package com.samreact.skooLLy.modules.fees.repository;

import com.samreact.skooLLy.modules.fees.entity.Invoice;
import com.samreact.skooLLy.modules.fees.entity.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @EntityGraph(attributePaths = {"items"})
    Optional<Invoice> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    @EntityGraph(attributePaths = {"items"})
    Page<Invoice> findAllBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    Page<Invoice> findAllByStudentIdAndSchoolIdAndDeletedFalse(
            Long studentId, Long schoolId, Pageable pageable);

    List<Invoice> findAllByStudentIdAndSchoolIdAndDeletedFalse(
            Long studentId, Long schoolId);

    Page<Invoice> findAllByStudentIdAndTermIdAndSchoolIdAndDeletedFalse(
            Long studentId, Long termId, Long schoolId, Pageable pageable);

    List<Invoice> findAllByStudentIdAndTermIdAndSchoolIdAndDeletedFalse(
            Long studentId, Long termId, Long schoolId);

    List<Invoice> findAllByStudentIdAndSessionIdAndTermIdAndSchoolIdAndDeletedFalse(
            Long studentId, Long sessionId, Long termId, Long schoolId);

    List<Invoice> findAllBySchoolIdAndTermIdAndDeletedFalse(
            Long schoolId, Long termId);

    boolean existsByStudentIdAndSessionIdAndTermIdAndSchoolIdAndDeletedFalse(
            Long studentId, Long sessionId, Long termId, Long schoolId);

    long countByStatusAndSchoolIdAndDeletedFalse(InvoiceStatus status, Long schoolId);

    long countBySchoolIdAndDeletedFalse(Long schoolId);

    Optional<Invoice> findByInvoiceNoAndSchoolId(String invoiceNo, Long schoolId);
}
