package com.samreact.skooLLy.modules.attendance.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.attendance.dto.AttendanceResponse;
import com.samreact.skooLLy.modules.attendance.dto.AttendanceSummaryResponse;
import com.samreact.skooLLy.modules.attendance.dto.BulkMarkAttendanceRequest;
import com.samreact.skooLLy.modules.attendance.dto.MarkAttendanceRequest;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse markAttendance(MarkAttendanceRequest request);

    List<AttendanceResponse> bulkMarkAttendance(BulkMarkAttendanceRequest request);

    AttendanceResponse getAttendanceById(Long id);

    PagedResponse<AttendanceResponse> getAttendanceByStudent(
            Long studentId, Long termId, int page, int size);

    PagedResponse<AttendanceResponse> getAttendanceByClassroom(
            Long classroomId, LocalDate date, int page, int size);

    List<AttendanceResponse> getAttendanceByClassroomAndDateRange(
            Long classroomId, LocalDate from, LocalDate to);

    AttendanceResponse updateAttendance(Long id, MarkAttendanceRequest request);

    void deleteAttendance(Long id);

    AttendanceSummaryResponse getStudentSummary(Long studentId, Long termId);

    AttendanceSummaryResponse getClassroomSummary(Long classroomId, Long termId);
}
