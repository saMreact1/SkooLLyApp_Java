package com.samreact.skooLLy.modules.academic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClassroomRequest {
    private String name;
    private String section;
    private String description;
    private Integer capacity;
    private Long classTeacherId;
    private String level;
    private Boolean active;
}