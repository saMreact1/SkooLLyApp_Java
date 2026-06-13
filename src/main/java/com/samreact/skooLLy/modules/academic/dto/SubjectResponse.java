package com.samreact.skooLLy.modules.academic.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubjectResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private String category;
    private boolean isElective;
    private boolean active;
    private String schoolName;
    private LocalDateTime createdAt;
}