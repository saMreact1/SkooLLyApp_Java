package com.samreact.skooLLy.modules.attendance.dto;

import com.samreact.skooLLy.modules.attendance.entity.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long classroomId;
    private String classroomName;
    private String classroomSection;
    private Long sessionId;
    private String sessionName;
    private Long termId;
    private String termName;
    private LocalDate date;
    private AttendanceStatus status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String remark;
    private String markedByName;
    private LocalDateTime createdAt;
}
