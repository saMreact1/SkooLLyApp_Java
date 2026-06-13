package com.samreact.skooLLy.modules.teacher.dto;

import com.samreact.skooLLy.modules.teacher.entity.enums.EmploymentType;
import com.samreact.skooLLy.modules.teacher.entity.enums.QualificationLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeacherRequestDTO {
    private QualificationLevel highestQualification;
    private String specialization;
    private Integer yearsOfExperience;
    private EmploymentType employmentType;
    private String designation;
}
