package com.samreact.skooLLy.modules.academic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samreact.skooLLy.modules.academic.entity.enums.TermStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TermResponse {

    private Long id;
    private Long sessionId;
    private String sessionName;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private TermStatus status;
    @JsonProperty("isCurrent")
    private boolean isCurrent;
    private LocalDateTime createdAt;
}