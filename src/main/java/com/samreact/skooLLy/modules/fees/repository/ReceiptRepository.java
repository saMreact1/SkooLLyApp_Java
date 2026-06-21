package com.samreact.skooLLy.modules.fees.repository;

import com.samreact.skooLLy.modules.fees.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByPaymentIdAndSchoolIdAndDeletedFalse(
            Long paymentId, Long schoolId);

    boolean existsByReceiptNoAndSchoolId(String receiptNo, Long schoolId);
}
