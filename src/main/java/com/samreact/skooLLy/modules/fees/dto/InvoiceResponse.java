package com.samreact.skooLLy.modules.fees.dto;

import com.samreact.skooLLy.modules.fees.entity.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class InvoiceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String currentClass;
    private Long sessionId;
    private String sessionName;
    private Long termId;
    private String termName;
    private String invoiceNo;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balance;
    private InvoiceStatus status;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;
}
