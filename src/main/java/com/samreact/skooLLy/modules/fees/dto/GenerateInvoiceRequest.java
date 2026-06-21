package com.samreact.skooLLy.modules.fees.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateInvoiceRequest {
    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Term ID is required")
    private Long termId;

    @NotNull(message = "Fee plan IDs are required")
    private List<Long> feePlanIds;
}
