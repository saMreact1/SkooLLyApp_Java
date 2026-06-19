package com.samreact.skooLLy.modules.attendance.dto;

import com.samreact.skooLLy.modules.attendance.entity.enums.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class BulkMarkAttendanceRequest {
    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Term ID is required")
    private Long termId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Valid
    @NotNull(message = "Student records are required")
    private List<StudentAttendanceRecord> records;

    @Getter
    @Setter
    public static class StudentAttendanceRecord {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Status is required")
        private AttendanceStatus status;

        private LocalTime checkInTime;

        private LocalTime checkOutTime;

        private String remark;
    }
}
