package com.samreact.skooLLy.modules.academic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSubjectRequest {

    private String name;
    private String code;
    private String description;
    private String category;
    private Boolean isElective;
    private Boolean active;
}