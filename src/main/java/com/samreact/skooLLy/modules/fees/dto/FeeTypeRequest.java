package com.samreact.skooLLy.modules.fees.dto;

import com.samreact.skooLLy.modules.fees.entity.enums.FeeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeTypeRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Category is required")
    private FeeCategory category;

    private String description;
}
