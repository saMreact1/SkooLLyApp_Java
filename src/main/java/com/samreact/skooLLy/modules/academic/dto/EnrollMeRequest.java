package com.samreact.skooLLy.modules.academic.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnrollMeRequest {

    @NotEmpty(message = "At least one subject must be selected")
    private List<Long> subjectIds;

    @NotNull(message = "Term ID is required")
    private Long termId;
}
