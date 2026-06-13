package com.samreact.skooLLy.modules.academic.dto;

import com.samreact.skooLLy.modules.academic.entity.enums.DayOfWeek;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@Builder
public class TimetableResponse {

    private Long id;
    private Long sessionId;
    private String sessionName;
    private Long termId;
    private String termName;
    private Long classroomId;
    private String classroomName;
    private String classroomSection;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
}