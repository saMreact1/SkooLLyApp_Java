package com.samreact.skooLLy.modules.grades.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GradingScaleResponse {
    private Long id;
    private String gradeLetter;
    private BigDecimal minPercentage;
    private BigDecimal maxPercentage;
    private String description;
}
