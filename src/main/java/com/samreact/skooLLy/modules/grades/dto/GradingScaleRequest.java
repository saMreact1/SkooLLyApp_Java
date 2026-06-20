package com.samreact.skooLLy.modules.grades.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GradingScaleRequest {
    @NotBlank(message = "Grade letter is required")
    private String gradeLetter;

    @NotNull(message = "Min percentage is required")
    @DecimalMin(value = "0", message = "Min percentage must be at least 0")
    private BigDecimal minPercentage;

    @NotNull(message = "Max percentage is required")
    @DecimalMin(value = "0", message = "Max percentage must be at least 0")
    private BigDecimal maxPercentage;

    private String description;
}
