package com.samreact.skooLLy.modules.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckEmailAndSchoolResponseDTO {
    private int nextStep;

    private String message;

    private boolean schoolExists;

    private Long schoolId;
}