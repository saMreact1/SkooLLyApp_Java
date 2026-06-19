package com.samreact.skooLLy.modules.academic.dto;

import com.samreact.skooLLy.modules.academic.entity.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EnrolledStudentResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}
