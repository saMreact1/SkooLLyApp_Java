package com.samreact.skooLLy.modules.fees.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.academic.entity.AcademicSession;
import com.samreact.skooLLy.modules.academic.entity.Classroom;
import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.repository.AcademicSessionRepository;
import com.samreact.skooLLy.modules.academic.repository.ClassroomRepository;
import com.samreact.skooLLy.modules.academic.repository.TermRepository;
import com.samreact.skooLLy.modules.fees.dto.*;
import com.samreact.skooLLy.modules.fees.entity.FeePlan;
import com.samreact.skooLLy.modules.fees.entity.Invoice;
import com.samreact.skooLLy.modules.fees.entity.InvoiceItem;
import com.samreact.skooLLy.modules.fees.entity.Payment;
import com.samreact.skooLLy.modules.fees.entity.enums.InvoiceStatus;
import com.samreact.skooLLy.modules.fees.repository.FeePlanRepository;
import com.samreact.skooLLy.modules.fees.repository.InvoiceRepository;
import com.samreact.skooLLy.modules.fees.repository.PaymentRepository;
import com.samreact.skooLLy.modules.fees.service.InvoiceService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final FeePlanRepository feePlanRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final ClassroomRepository classroomRepository;
    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final CurrentUserService currentUserService;

    private static final AtomicLong invoiceCounter = new AtomicLong(0);

    @Override
    @Transactional
    public List<InvoiceResponse> generateInvoices(GenerateInvoiceRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Classroom classroom = classroomRepository.findByIdAndSchoolId(
                request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", request.getClassroomId()));

        AcademicSession session = sessionRepository.findByIdAndSchoolId(
                request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", request.getSessionId()));

        Term term = termRepository.findByIdAndSchoolId(
                request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", request.getTermId()));

        List<FeePlan> feePlans = feePlanRepository
                .findAllByIdInAndSchoolIdAndDeletedFalse(request.getFeePlanIds(), schoolId);

        if (feePlans.isEmpty()) {
            throw new BusinessException("No valid fee plans found", HttpStatus.BAD_REQUEST);
        }

        List<Student> students = studentRepository
                .findAllBySchoolIdAndDeleted(schoolId, false)
                .stream()
                .filter(s -> {
                    String studentClass = s.getCurrentClass() != null
                            ? s.getCurrentClass().trim().toLowerCase() : "";
                    String classroomName = classroom.getName() != null
                            ? classroom.getName().trim().toLowerCase() : "";
                    return studentClass.equals(classroomName)
                            || studentClass.startsWith(classroomName);
                })
                .toList();

        if (students.isEmpty()) {
            throw new BusinessException("No students found in this class", HttpStatus.BAD_REQUEST);
        }

        List<InvoiceResponse> responses = new ArrayList<>();

        for (Student student : students) {
            List<Invoice> existingInvoices = invoiceRepository
                    .findAllByStudentIdAndSessionIdAndTermIdAndSchoolIdAndDeletedFalse(
                            student.getId(), session.getId(), term.getId(), schoolId);

            if (!existingInvoices.isEmpty()) {
                Invoice existingInvoice = existingInvoices.get(0);

                List<Long> existingFeePlanIds = existingInvoice.getItems().stream()
                        .map(item -> item.getFeePlan().getId())
                        .toList();

                List<FeePlan> newPlans = feePlans.stream()
                        .filter(fp -> !existingFeePlanIds.contains(fp.getId()))
                        .toList();

                if (newPlans.isEmpty()) {
                    log.warn("All fee plans already on invoice {} for student {}, skipping",
                            existingInvoice.getInvoiceNo(), student.getAdmissionNumber());
                    responses.add(mapToInvoiceResponse(existingInvoice));
                    continue;
                }

                for (FeePlan feePlan : newPlans) {
                    InvoiceItem item = InvoiceItem.builder()
                            .invoice(existingInvoice)
                            .feePlan(feePlan)
                            .feeTypeName(feePlan.getFeeType().getName())
                            .amount(feePlan.getAmount())
                            .build();
                    existingInvoice.getItems().add(item);
                }

                BigDecimal newTotal = existingInvoice.getItems().stream()
                        .map(InvoiceItem::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                existingInvoice.setTotalAmount(newTotal);

                if (existingInvoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                    if (existingInvoice.getPaidAmount().compareTo(newTotal) >= 0) {
                        existingInvoice.setStatus(InvoiceStatus.PAID);
                    } else {
                        existingInvoice.setStatus(InvoiceStatus.PARTIAL);
                    }
                }

                Invoice updated = invoiceRepository.save(existingInvoice);
                log.info("Added {} new fee plan(s) to invoice {} for student {}",
                        newPlans.size(), updated.getInvoiceNo(), student.getAdmissionNumber());
                responses.add(mapToInvoiceResponse(updated));
                continue;
            }

            BigDecimal totalAmount = feePlans.stream()
                    .map(FeePlan::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Invoice invoice = Invoice.builder()
                    .school(school)
                    .student(student)
                    .session(session)
                    .term(term)
                    .invoiceNo(generateInvoiceNo(schoolId))
                    .totalAmount(totalAmount)
                    .paidAmount(BigDecimal.ZERO)
                    .status(InvoiceStatus.PENDING)
                    .build();

            List<InvoiceItem> items = new ArrayList<>();
            for (FeePlan feePlan : feePlans) {
                InvoiceItem item = InvoiceItem.builder()
                        .invoice(invoice)
                        .feePlan(feePlan)
                        .feeTypeName(feePlan.getFeeType().getName())
                        .amount(feePlan.getAmount())
                        .build();
                items.add(item);
            }
            invoice.setItems(items);

            Invoice saved = invoiceRepository.save(invoice);
            log.info("Invoice generated: {} for student {}",
                    saved.getInvoiceNo(), student.getAdmissionNumber());

            responses.add(mapToInvoiceResponse(saved));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Invoice invoice = invoiceRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        return mapToInvoiceResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceResponse> getAllInvoices(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<Invoice> invoicePage = invoiceRepository
                .findAllBySchoolIdAndDeletedFalse(schoolId, PageRequest.of(page, size));

        return PageUtil.from(invoicePage, this::mapToInvoiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceResponse> getInvoicesByStudent(
            Long studentId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        studentRepository.findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Page<Invoice> invoicePage = invoiceRepository
                .findAllByStudentIdAndSchoolIdAndDeletedFalse(
                        studentId, schoolId, PageRequest.of(page, size));

        return PageUtil.from(invoicePage, this::mapToInvoiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceResponse> getInvoicesByStudentAndTerm(
            Long studentId, Long termId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        studentRepository.findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Page<Invoice> invoicePage = invoiceRepository
                .findAllByStudentIdAndTermIdAndSchoolIdAndDeletedFalse(
                        studentId, termId, schoolId, PageRequest.of(page, size));

        return PageUtil.from(invoicePage, this::mapToInvoiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentBalanceResponse getStudentBalance(Long studentId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Student student = studentRepository.findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Invoice> invoices = invoiceRepository
                .findAllByStudentIdAndSchoolIdAndDeletedFalse(studentId, schoolId);

        BigDecimal totalInvoiced = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = invoices.stream()
                .map(Invoice::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<InvoiceResponse> invoiceResponses = invoices.stream()
                .map(this::mapToInvoiceResponse)
                .toList();

        return StudentBalanceResponse.builder()
                .studentId(student.getId())
                .studentName(student.getUser().getFirstName()
                        + " " + student.getUser().getLastName())
                .admissionNumber(student.getAdmissionNumber())
                .totalInvoiced(totalInvoiced)
                .totalPaid(totalPaid)
                .outstandingBalance(totalInvoiced.subtract(totalPaid))
                .invoices(invoiceResponses)
                .build();
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Invoice invoice = invoiceRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        invoice.setDeleted(true);
        invoiceRepository.save(invoice);
        log.info("Invoice deleted: {}", id);
    }

    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        List<Payment> payments = paymentRepository
                .findAllByInvoiceIdAndDeletedFalse(invoice.getId());

        BigDecimal paidAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .feePlanId(item.getFeePlan().getId())
                        .feeTypeName(item.getFeeTypeName())
                        .amount(item.getAmount())
                        .build())
                .toList();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .studentId(invoice.getStudent().getId())
                .studentName(invoice.getStudent().getUser().getFirstName()
                        + " " + invoice.getStudent().getUser().getLastName())
                .admissionNumber(invoice.getStudent().getAdmissionNumber())
                .currentClass(invoice.getStudent().getCurrentClass())
                .sessionId(invoice.getSession().getId())
                .sessionName(invoice.getSession().getName())
                .termId(invoice.getTerm().getId())
                .termName(invoice.getTerm().getName())
                .invoiceNo(invoice.getInvoiceNo())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(paidAmount)
                .balance(invoice.getTotalAmount().subtract(paidAmount))
                .status(invoice.getStatus())
                .items(itemResponses)
                .createdAt(invoice.getCreatedAt())
                .build();
    }

    private String generateInvoiceNo(Long schoolId) {
        long num = invoiceCounter.incrementAndGet();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "INV-" + schoolId + "-" + ts + "-" + num;
    }
}
