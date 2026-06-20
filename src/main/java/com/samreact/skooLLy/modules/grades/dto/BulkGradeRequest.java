package com.samreact.skooLLy.modules.grades.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BulkGradeRequest {
    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotEmpty(message = "Records list cannot be empty")
    private List<StudentGradeRecord> records;

    @Getter
    @Setter
    public static class StudentGradeRecord {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Marks obtained is required")
        @DecimalMin(value = "0", message = "Marks cannot be negative")
        private BigDecimal marksObtained;

        private String remark;
    }
}
