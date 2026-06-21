package com.samreact.skooLLy.modules.fees.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FeePlanRequest {
    @NotNull(message = "Fee type ID is required")
    private Long feeTypeId;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Term ID is required")
    private Long termId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private boolean isOptional;

    private String description;
}
