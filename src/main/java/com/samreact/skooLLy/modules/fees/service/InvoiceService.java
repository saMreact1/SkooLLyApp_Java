package com.samreact.skooLLy.modules.fees.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.GenerateInvoiceRequest;
import com.samreact.skooLLy.modules.fees.dto.InvoiceResponse;
import com.samreact.skooLLy.modules.fees.dto.StudentBalanceResponse;

import java.util.List;

public interface InvoiceService {
    List<InvoiceResponse> generateInvoices(GenerateInvoiceRequest request);

    InvoiceResponse getInvoiceById(Long id);

    PagedResponse<InvoiceResponse> getAllInvoices(int page, int size);

    PagedResponse<InvoiceResponse> getInvoicesByStudent(
            Long studentId, int page, int size);

    PagedResponse<InvoiceResponse> getInvoicesByStudentAndTerm(
            Long studentId, Long termId, int page, int size);

    StudentBalanceResponse getStudentBalance(Long studentId);

    void deleteInvoice(Long id);
}
