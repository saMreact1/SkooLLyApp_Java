package com.samreact.skooLLy.modules.grades.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ReportCardResponse {
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String classroomName;
    private String sessionName;
    private String termName;
    private List<SubjectResult> results;
    private BigDecimal totalMarksObtained;
    private BigDecimal totalMaxMarks;
    private BigDecimal overallPercentage;
    private String overallGrade;
    private int rank;

    @Getter
    @Builder
    public static class SubjectResult {
        private Long subjectId;
        private String subjectName;
        private BigDecimal marksObtained;
        private Integer maxMarks;
        private BigDecimal percentage;
        private String letterGrade;
        private int examCount;
    }
}
