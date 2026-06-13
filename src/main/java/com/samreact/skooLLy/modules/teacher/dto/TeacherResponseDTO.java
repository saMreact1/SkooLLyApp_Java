package com.samreact.skooLLy.modules.teacher.dto;

import com.samreact.skooLLy.modules.teacher.entity.enums.EmploymentType;
import com.samreact.skooLLy.modules.teacher.entity.enums.QualificationLevel;
import com.samreact.skooLLy.modules.teacher.entity.enums.TeacherStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TeacherResponseDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String staffId;
    private LocalDate joinDate;
    private QualificationLevel highestQualification;
    private String specialization;
    private Integer yearsOfExperience;
    private EmploymentType employmentType;
    private String designation;
    private TeacherStatus status;
    private String schoolName;
    private LocalDateTime createdAt;
}
