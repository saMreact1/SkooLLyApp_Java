package com.samreact.skooLLy.modules.grades.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class GradeSheetResponse {
    private Long examId;
    private String examName;
    private String examType;
    private Long classroomId;
    private String classroomName;
    private Long subjectId;
    private String subjectName;
    private Long termId;
    private String termName;
    private Integer maxMarks;
    private List<StudentGradeEntry> grades;
    private BigDecimal classAverage;
    private BigDecimal highestMark;
    private BigDecimal lowestMark;

    @Getter
    @Builder
    public static class StudentGradeEntry {
        private Long studentId;
        private String studentName;
        private String admissionNumber;
        private BigDecimal marksObtained;
        private BigDecimal percentage;
        private String letterGrade;
        private String remark;
    }
}
