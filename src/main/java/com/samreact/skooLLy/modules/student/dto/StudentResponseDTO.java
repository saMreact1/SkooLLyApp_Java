package com.samreact.skooLLy.modules.student.dto;

import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class StudentResponseDTO {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String admissionNumber;
    private LocalDate admissionDate;
    private String currentClass;
    private String currentSection;
    private StudentStatus status;
    private String bloodGroup;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String schoolName;
    private LocalDateTime createdAt;
}