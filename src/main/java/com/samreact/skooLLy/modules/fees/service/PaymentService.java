package com.samreact.skooLLy.modules.fees.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.PaymentRequest;
import com.samreact.skooLLy.modules.fees.dto.PaymentResponse;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    PaymentResponse recordPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    PagedResponse<PaymentResponse> getAllPayments(int page, int size);

    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);

    PagedResponse<PaymentResponse> getPaymentsByDateRange(
            LocalDate from, LocalDate to, int page, int size);

    void deletePayment(Long id);
}
