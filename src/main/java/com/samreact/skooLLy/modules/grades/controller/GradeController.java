package com.samreact.skooLLy.modules.grades.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.grades.dto.BulkGradeRequest;
import com.samreact.skooLLy.modules.grades.dto.GradeRequest;
import com.samreact.skooLLy.modules.grades.dto.GradeResponse;
import com.samreact.skooLLy.modules.grades.dto.GradeSheetResponse;
import com.samreact.skooLLy.modules.grades.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<GradeResponse>> addGrade(
            @Valid @RequestBody GradeRequest request) {
        GradeResponse response = gradeService.addGrade(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grade added successfully", response));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> bulkAddGrades(
            @Valid @RequestBody BulkGradeRequest request) {
        List<GradeResponse> responses = gradeService.bulkAddGrades(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk grades added successfully", responses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<GradeResponse>> getGradeById(@PathVariable Long id) {
        GradeResponse response = gradeService.getGradeById(id);
        return ResponseEntity.ok(ApiResponse.success("Grade retrieved successfully", response));
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PagedResponse<GradeResponse>>> getGradesByExam(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PagedResponse<GradeResponse> response = gradeService.getGradesByExam(examId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved successfully", response));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<GradeResponse>>> getGradesByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<GradeResponse> response = gradeService.getGradesByStudent(studentId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved successfully", response));
    }

    @GetMapping("/sheet/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<GradeSheetResponse>> getGradeSheet(@PathVariable Long examId) {
        GradeSheetResponse response = gradeService.getGradeSheet(examId);
        return ResponseEntity.ok(ApiResponse.success("Grade sheet retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        GradeResponse response = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(ApiResponse.success("Grade updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok(ApiResponse.success("Grade deleted successfully"));
    }
}
