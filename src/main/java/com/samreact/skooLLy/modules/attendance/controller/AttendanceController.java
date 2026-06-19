package com.samreact.skooLLy.modules.attendance.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.attendance.dto.AttendanceResponse;
import com.samreact.skooLLy.modules.attendance.dto.AttendanceSummaryResponse;
import com.samreact.skooLLy.modules.attendance.dto.BulkMarkAttendanceRequest;
import com.samreact.skooLLy.modules.attendance.dto.MarkAttendanceRequest;
import com.samreact.skooLLy.modules.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request) {

        AttendanceResponse response = attendanceService.markAttendance(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Attendance marked successfully", response));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> bulkMarkAttendance(
            @Valid @RequestBody BulkMarkAttendanceRequest request) {

        List<AttendanceResponse> responses =
                attendanceService.bulkMarkAttendance(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Bulk attendance marked successfully", responses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(
            @PathVariable Long id) {

        AttendanceResponse response = attendanceService.getAttendanceById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance retrieved successfully", response));
    }

    @GetMapping("/student/{studentId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<AttendanceResponse>>> getAttendanceByStudent(
            @PathVariable Long studentId,
            @PathVariable Long termId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<AttendanceResponse> responses =
                attendanceService.getAttendanceByStudent(
                        studentId, termId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance records retrieved successfully",
                        responses));
    }

    @GetMapping("/classroom/{classroomId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PagedResponse<AttendanceResponse>>> getAttendanceByClassroom(
            @PathVariable Long classroomId,
            @PathVariable LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<AttendanceResponse> responses =
                attendanceService.getAttendanceByClassroom(
                        classroomId, date, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance records retrieved successfully",
                        responses));
    }

    @GetMapping("/classroom/{classroomId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendanceByClassroomAndDateRange(
            @PathVariable Long classroomId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        List<AttendanceResponse> responses =
                attendanceService.getAttendanceByClassroomAndDateRange(
                        classroomId, from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance records retrieved successfully",
                        responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody MarkAttendanceRequest request) {

        AttendanceResponse response =
                attendanceService.updateAttendance(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteAttendance(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance deleted successfully"));
    }

    @GetMapping("/student/{studentId}/term/{termId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AttendanceSummaryResponse>> getStudentAttendanceSummary(
            @PathVariable Long studentId,
            @PathVariable Long termId) {

        AttendanceSummaryResponse summary =
                attendanceService.getStudentSummary(studentId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance summary retrieved successfully",
                        summary));
    }

    @GetMapping("/classroom/{classroomId}/term/{termId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceSummaryResponse>> getClassroomAttendanceSummary(
            @PathVariable Long classroomId,
            @PathVariable Long termId) {

        AttendanceSummaryResponse summary =
                attendanceService.getClassroomSummary(classroomId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classroom attendance summary retrieved successfully",
                        summary));
    }
}
