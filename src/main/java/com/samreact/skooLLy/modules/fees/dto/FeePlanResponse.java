package com.samreact.skooLLy.modules.fees.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class FeePlanResponse {
    private Long id;
    private Long feeTypeId;
    private String feeTypeName;
    private FeeTypeResponse feeType;
    private Long classroomId;
    private String classroomName;
    private String classroomSection;
    private Long sessionId;
    private String sessionName;
    private Long termId;
    private String termName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private boolean isOptional;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
}
