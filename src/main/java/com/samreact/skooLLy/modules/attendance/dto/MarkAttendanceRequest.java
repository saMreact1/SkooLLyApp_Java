package com.samreact.skooLLy.modules.attendance.dto;

import com.samreact.skooLLy.modules.attendance.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class MarkAttendanceRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Term ID is required")
    private Long termId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private String remark;
}
