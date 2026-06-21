package com.samreact.skooLLy.modules.fees.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.FeePlanRequest;
import com.samreact.skooLLy.modules.fees.dto.FeePlanResponse;
import com.samreact.skooLLy.modules.fees.service.FeePlanService;
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
@RequestMapping("/api/fees/plans")
@RequiredArgsConstructor
public class FeePlanController {

    private final FeePlanService feePlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeePlanResponse>> createFeePlan(
            @Valid @RequestBody FeePlanRequest request) {

        FeePlanResponse response = feePlanService.createFeePlan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fee plan created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeePlanResponse>> getFeePlanById(
            @PathVariable Long id) {

        FeePlanResponse response = feePlanService.getFeePlanById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Fee plan retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<FeePlanResponse>>> getAllFeePlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<FeePlanResponse> responses = feePlanService.getAllFeePlans(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Fee plans retrieved successfully", responses));
    }

    @GetMapping("/classroom/{classroomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeePlanResponse>>> getFeePlansByClassroom(
            @PathVariable Long classroomId) {

        List<FeePlanResponse> responses = feePlanService.getFeePlansByClassroom(classroomId);

        return ResponseEntity.ok(
                ApiResponse.success("Fee plans retrieved successfully", responses));
    }

    @GetMapping("/classroom/{classroomId}/term/{termId}/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeePlanResponse>>> getFeePlansByClassroomAndTerm(
            @PathVariable Long classroomId,
            @PathVariable Long termId,
            @PathVariable Long sessionId) {

        List<FeePlanResponse> responses = feePlanService.getFeePlansByClassroomAndTerm(
                classroomId, termId, sessionId);

        return ResponseEntity.ok(
                ApiResponse.success("Fee plans retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeePlanResponse>> updateFeePlan(
            @PathVariable Long id,
            @Valid @RequestBody FeePlanRequest request) {

        FeePlanResponse response = feePlanService.updateFeePlan(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Fee plan updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteFeePlan(
            @PathVariable Long id) {

        feePlanService.deleteFeePlan(id);

        return ResponseEntity.ok(
                ApiResponse.success("Fee plan deleted successfully"));
    }
}
