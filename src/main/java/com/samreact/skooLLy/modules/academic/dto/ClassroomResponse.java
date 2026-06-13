package com.samreact.skooLLy.modules.academic.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClassroomResponse {
    private Long id;
    private String name;
    private String section;
    private String description;
    private Integer capacity;
    private Long classTeacherId;
    private String classTeacherName;
    private String level;
    private boolean active;
    private String schoolName;
    private LocalDateTime createdAt;
}