package com.samreact.skooLLy.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClassroomRequest {
    @NotBlank(message = "Classroom name is required")
    private String name;

    @NotBlank(message = "Section is required")
    private String section;

    private String description;
    private Integer capacity;
    private Long classTeacherId;
    private String level;
}