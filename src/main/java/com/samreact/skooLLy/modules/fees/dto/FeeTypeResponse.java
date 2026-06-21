package com.samreact.skooLLy.modules.fees.dto;

import com.samreact.skooLLy.modules.fees.entity.enums.FeeCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeeTypeResponse {
    private Long id;
    private String name;
    private FeeCategory category;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
}
