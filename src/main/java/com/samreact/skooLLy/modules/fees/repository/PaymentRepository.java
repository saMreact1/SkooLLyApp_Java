package com.samreact.skooLLy.modules.fees.repository;

import com.samreact.skooLLy.modules.fees.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndSchoolIdAndDeletedFalse(Long id, Long schoolId);

    Page<Payment> findAllBySchoolIdAndDeletedFalse(Long schoolId, Pageable pageable);

    List<Payment> findAllByInvoiceIdAndSchoolIdAndDeletedFalse(
            Long invoiceId, Long schoolId);

    List<Payment> findAllByInvoiceIdAndDeletedFalse(Long invoiceId);

    Page<Payment> findAllByPaymentDateBetweenAndSchoolIdAndDeletedFalse(
            LocalDate from, LocalDate to, Long schoolId, Pageable pageable);
}
