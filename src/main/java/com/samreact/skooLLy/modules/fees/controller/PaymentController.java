package com.samreact.skooLLy.modules.fees.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.PaymentRequest;
import com.samreact.skooLLy.modules.fees.dto.PaymentResponse;
import com.samreact.skooLLy.modules.fees.service.PaymentService;
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
@RequestMapping("/api/fees/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.recordPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long id) {

        PaymentResponse response = paymentService.getPaymentById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Payment retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<PaymentResponse> responses = paymentService.getAllPayments(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Payments retrieved successfully", responses));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByInvoice(
            @PathVariable Long invoiceId) {

        List<PaymentResponse> responses = paymentService.getPaymentsByInvoice(invoiceId);

        return ResponseEntity.ok(
                ApiResponse.success("Payments retrieved successfully", responses));
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getPaymentsByDateRange(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<PaymentResponse> responses = paymentService.getPaymentsByDateRange(
                from, to, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Payments retrieved successfully", responses));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deletePayment(
            @PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.ok(
                ApiResponse.success("Payment deleted successfully"));
    }
}
