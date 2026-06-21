package com.samreact.skooLLy.modules.fees.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class InvoiceItemResponse {
    private Long id;
    private Long feePlanId;
    private String feeTypeName;
    private BigDecimal amount;
}
