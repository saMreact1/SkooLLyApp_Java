package com.samreact.skooLLy.modules.fees.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class StudentBalanceResponse {
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private BigDecimal totalInvoiced;
    private BigDecimal totalPaid;
    private BigDecimal outstandingBalance;
    private List<InvoiceResponse> invoices;
}
