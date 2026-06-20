package com.samreact.skooLLy.modules.grades.dto;

import com.samreact.skooLLy.modules.grades.entity.enums.ExamType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ExamResponse {
    private Long id;
    private String name;
    private ExamType examType;
    private Long classroomId;
    private String classroomName;
    private String classroomSection;
    private Long subjectId;
    private String subjectName;
    private Long sessionId;
    private String sessionName;
    private Long termId;
    private String termName;
    private LocalDate examDate;
    private Integer maxMarks;
    private Integer weightage;
    private String description;
    private boolean published;
    private int totalGrades;
    private LocalDateTime createdAt;
}
