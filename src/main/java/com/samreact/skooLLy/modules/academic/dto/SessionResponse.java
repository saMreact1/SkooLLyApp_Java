package com.samreact.skooLLy.modules.academic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samreact.skooLLy.modules.academic.entity.enums.SessionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SessionResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private SessionStatus status;
    @JsonProperty("isCurrent")
    private boolean isCurrent;
    private String schoolName;
    private List<TermResponse> terms;
    private LocalDateTime createdAt;
}