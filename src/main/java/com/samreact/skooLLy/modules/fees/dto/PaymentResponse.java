package com.samreact.skooLLy.modules.fees.dto;

import com.samreact.skooLLy.modules.fees.entity.enums.PaymentMode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long invoiceId;
    private String invoiceNo;
    private String studentName;
    private String admissionNumber;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String reference;
    private LocalDate paymentDate;
    private String collectedByName;
    private String remark;
    private String receiptNo;
    private LocalDateTime createdAt;
}
