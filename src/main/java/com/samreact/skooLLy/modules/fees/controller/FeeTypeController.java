package com.samreact.skooLLy.modules.fees.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeRequest;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeResponse;
import com.samreact.skooLLy.modules.fees.service.FeeTypeService;
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
@RequestMapping("/api/fees/types")
@RequiredArgsConstructor
public class FeeTypeController {

    private final FeeTypeService feeTypeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeeTypeResponse>> createFeeType(
            @Valid @RequestBody FeeTypeRequest request) {

        FeeTypeResponse response = feeTypeService.createFeeType(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fee type created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeeTypeResponse>> getFeeTypeById(
            @PathVariable Long id) {

        FeeTypeResponse response = feeTypeService.getFeeTypeById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Fee type retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<FeeTypeResponse>>> getAllFeeTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<FeeTypeResponse> responses = feeTypeService.getAllFeeTypes(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Fee types retrieved successfully", responses));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeeTypeResponse>>> getAllActiveFeeTypes() {

        List<FeeTypeResponse> responses = feeTypeService.getAllActiveFeeTypes();

        return ResponseEntity.ok(
                ApiResponse.success("Active fee types retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeeTypeResponse>> updateFeeType(
            @PathVariable Long id,
            @Valid @RequestBody FeeTypeRequest request) {

        FeeTypeResponse response = feeTypeService.updateFeeType(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Fee type updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteFeeType(
            @PathVariable Long id) {

        feeTypeService.deleteFeeType(id);

        return ResponseEntity.ok(
                ApiResponse.success("Fee type deleted successfully"));
    }
}
