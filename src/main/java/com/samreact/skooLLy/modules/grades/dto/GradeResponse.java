package com.samreact.skooLLy.modules.grades.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class GradeResponse {
    private Long id;
    private Long examId;
    private String examName;
    private String examType;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private BigDecimal marksObtained;
    private Integer maxMarks;
    private BigDecimal percentage;
    private String letterGrade;
    private String remark;
    private LocalDateTime createdAt;
}
