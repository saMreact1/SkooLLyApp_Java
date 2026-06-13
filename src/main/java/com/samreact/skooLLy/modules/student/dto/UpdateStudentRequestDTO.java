package com.samreact.skooLLy.modules.student.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentRequestDTO {

    private String currentClass;
    private String currentSection;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String bloodGroup;
    private String medicalConditions;
}