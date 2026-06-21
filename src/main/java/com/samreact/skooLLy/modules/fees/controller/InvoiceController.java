package com.samreact.skooLLy.modules.fees.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.GenerateInvoiceRequest;
import com.samreact.skooLLy.modules.fees.dto.InvoiceResponse;
import com.samreact.skooLLy.modules.fees.dto.StudentBalanceResponse;
import com.samreact.skooLLy.modules.fees.service.InvoiceService;
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
@RequestMapping("/api/fees/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> generateInvoices(
            @Valid @RequestBody GenerateInvoiceRequest request) {

        List<InvoiceResponse> responses = invoiceService.generateInvoices(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoices generated successfully", responses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(
            @PathVariable Long id) {

        InvoiceResponse response = invoiceService.getInvoiceById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Invoice retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceResponse>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<InvoiceResponse> responses = invoiceService.getAllInvoices(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Invoices retrieved successfully", responses));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceResponse>>> getInvoicesByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<InvoiceResponse> responses = invoiceService.getInvoicesByStudent(
                studentId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Invoices retrieved successfully", responses));
    }

    @GetMapping("/student/{studentId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceResponse>>> getInvoicesByStudentAndTerm(
            @PathVariable Long studentId,
            @PathVariable Long termId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<InvoiceResponse> responses = invoiceService.getInvoicesByStudentAndTerm(
                studentId, termId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Invoices retrieved successfully", responses));
    }

    @GetMapping("/student/{studentId}/balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<StudentBalanceResponse>> getStudentBalance(
            @PathVariable Long studentId) {

        StudentBalanceResponse response = invoiceService.getStudentBalance(studentId);

        return ResponseEntity.ok(
                ApiResponse.success("Student balance retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteInvoice(
            @PathVariable Long id) {

        invoiceService.deleteInvoice(id);

        return ResponseEntity.ok(
                ApiResponse.success("Invoice deleted successfully"));
    }
}
