package com.samreact.skooLLy.modules.grades.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.modules.grades.dto.GradingScaleRequest;
import com.samreact.skooLLy.modules.grades.dto.GradingScaleResponse;
import com.samreact.skooLLy.modules.grades.dto.ReportCardResponse;
import com.samreact.skooLLy.modules.grades.service.GradingScaleService;
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
@RequestMapping("/api/grading-scales")
@RequiredArgsConstructor
public class GradingScaleController {

    private final GradingScaleService gradingScaleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<GradingScaleResponse>> createGradingScale(
            @Valid @RequestBody GradingScaleRequest request) {
        GradingScaleResponse response = gradingScaleService.createGradingScale(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grading scale created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<GradingScaleResponse>>> getGradingScales() {
        List<GradingScaleResponse> response = gradingScaleService.getGradingScales();
        return ResponseEntity.ok(ApiResponse.success("Grading scales retrieved successfully", response));
    }

    @GetMapping("/report-card/student/{studentId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<ReportCardResponse>> getReportCard(
            @PathVariable Long studentId,
            @PathVariable Long termId) {
        ReportCardResponse response = gradingScaleService.getReportCard(studentId, termId);
        return ResponseEntity.ok(ApiResponse.success("Report card retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteGradingScale(@PathVariable Long id) {
        gradingScaleService.deleteGradingScale(id);
        return ResponseEntity.ok(ApiResponse.success("Grading scale deleted successfully"));
    }
}
