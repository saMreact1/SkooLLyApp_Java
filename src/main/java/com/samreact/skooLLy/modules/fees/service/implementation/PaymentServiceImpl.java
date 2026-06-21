package com.samreact.skooLLy.modules.fees.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.fees.dto.PaymentRequest;
import com.samreact.skooLLy.modules.fees.dto.PaymentResponse;
import com.samreact.skooLLy.modules.fees.entity.Invoice;
import com.samreact.skooLLy.modules.fees.entity.Payment;
import com.samreact.skooLLy.modules.fees.entity.Receipt;
import com.samreact.skooLLy.modules.fees.entity.enums.InvoiceStatus;
import com.samreact.skooLLy.modules.fees.repository.InvoiceRepository;
import com.samreact.skooLLy.modules.fees.repository.PaymentRepository;
import com.samreact.skooLLy.modules.fees.repository.ReceiptRepository;
import com.samreact.skooLLy.modules.fees.service.PaymentService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptRepository receiptRepository;
    private final SchoolRepository schoolRepository;
    private final CurrentUserService currentUserService;

    private static final AtomicLong receiptCounter = new AtomicLong(0);

    @Override
    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        User currentUser = currentUserService.getCurrentUser();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Invoice invoice = invoiceRepository
                .findByIdAndSchoolIdAndDeletedFalse(request.getInvoiceId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cannot pay a cancelled invoice", HttpStatus.BAD_REQUEST);
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Invoice is already fully paid", HttpStatus.BAD_REQUEST);
        }

        BigDecimal currentPaid = invoice.getPaidAmount();
        BigDecimal newPaid = currentPaid.add(request.getAmount());

        if (newPaid.compareTo(invoice.getTotalAmount()) > 0) {
            throw new BusinessException(
                    "Payment amount exceeds remaining balance. Remaining: "
                            + invoice.getTotalAmount().subtract(currentPaid),
                    HttpStatus.BAD_REQUEST);
        }

        Payment payment = Payment.builder()
                .school(school)
                .invoice(invoice)
                .amount(request.getAmount())
                .paymentMode(request.getPaymentMode())
                .reference(request.getReference())
                .paymentDate(request.getPaymentDate())
                .collectedBy(currentUser)
                .remark(request.getRemark())
                .build();

        Payment saved = paymentRepository.save(payment);

        invoice.setPaidAmount(newPaid);

        if (newPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIAL);
        }

        invoiceRepository.save(invoice);

        String receiptNo = generateReceiptNo(schoolId);
        Receipt receipt = Receipt.builder()
                .school(school)
                .payment(saved)
                .receiptNo(receiptNo)
                .build();

        receiptRepository.save(receipt);

        log.info("Payment recorded: {} for invoice {} receipt {}",
                request.getAmount(), invoice.getInvoiceNo(), receiptNo);

        return mapToPaymentResponse(saved, receiptNo);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Payment payment = paymentRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        String receiptNo = receiptRepository
                .findByPaymentIdAndSchoolIdAndDeletedFalse(payment.getId(), schoolId)
                .map(Receipt::getReceiptNo)
                .orElse(null);

        return mapToPaymentResponse(payment, receiptNo);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> getAllPayments(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<Payment> paymentPage = paymentRepository
                .findAllBySchoolIdAndDeletedFalse(schoolId, PageRequest.of(page, size));

        return PageUtil.from(paymentPage, p -> {
            String receiptNo = receiptRepository
                    .findByPaymentIdAndSchoolIdAndDeletedFalse(p.getId(), schoolId)
                    .map(Receipt::getReceiptNo)
                    .orElse(null);
            return mapToPaymentResponse(p, receiptNo);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        invoiceRepository.findByIdAndSchoolIdAndDeletedFalse(invoiceId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        return paymentRepository
                .findAllByInvoiceIdAndSchoolIdAndDeletedFalse(invoiceId, schoolId)
                .stream()
                .map(p -> {
                    String receiptNo = receiptRepository
                            .findByPaymentIdAndSchoolIdAndDeletedFalse(p.getId(), schoolId)
                            .map(Receipt::getReceiptNo)
                            .orElse(null);
                    return mapToPaymentResponse(p, receiptNo);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> getPaymentsByDateRange(
            LocalDate from, LocalDate to, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<Payment> paymentPage = paymentRepository
                .findAllByPaymentDateBetweenAndSchoolIdAndDeletedFalse(
                        from, to, schoolId, PageRequest.of(page, size));

        return PageUtil.from(paymentPage, p -> {
            String receiptNo = receiptRepository
                    .findByPaymentIdAndSchoolIdAndDeletedFalse(p.getId(), schoolId)
                    .map(Receipt::getReceiptNo)
                    .orElse(null);
            return mapToPaymentResponse(p, receiptNo);
        });
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Payment payment = paymentRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        payment.setDeleted(true);
        paymentRepository.save(payment);
        log.info("Payment deleted: {}", id);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment, String receiptNo) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNo(payment.getInvoice().getInvoiceNo())
                .studentName(payment.getInvoice().getStudent().getUser().getFirstName()
                        + " " + payment.getInvoice().getStudent().getUser().getLastName())
                .admissionNumber(payment.getInvoice().getStudent().getAdmissionNumber())
                .amount(payment.getAmount())
                .paymentMode(payment.getPaymentMode())
                .reference(payment.getReference())
                .paymentDate(payment.getPaymentDate())
                .collectedByName(payment.getCollectedBy().getFirstName()
                        + " " + payment.getCollectedBy().getLastName())
                .remark(payment.getRemark())
                .receiptNo(receiptNo)
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private String generateReceiptNo(Long schoolId) {
        long num = receiptCounter.incrementAndGet();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "RCP-" + schoolId + "-" + ts + "-" + num;
    }
}
