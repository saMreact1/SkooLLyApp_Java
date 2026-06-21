package com.samreact.skooLLy.modules.fees.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.academic.entity.AcademicSession;
import com.samreact.skooLLy.modules.academic.entity.Classroom;
import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.repository.AcademicSessionRepository;
import com.samreact.skooLLy.modules.academic.repository.ClassroomRepository;
import com.samreact.skooLLy.modules.academic.repository.TermRepository;
import com.samreact.skooLLy.modules.fees.dto.FeePlanRequest;
import com.samreact.skooLLy.modules.fees.dto.FeePlanResponse;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeResponse;
import com.samreact.skooLLy.modules.fees.entity.FeePlan;
import com.samreact.skooLLy.modules.fees.entity.FeeType;
import com.samreact.skooLLy.modules.fees.repository.FeePlanRepository;
import com.samreact.skooLLy.modules.fees.repository.FeeTypeRepository;
import com.samreact.skooLLy.modules.fees.service.FeePlanService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeePlanServiceImpl implements FeePlanService {

    private final FeePlanRepository feePlanRepository;
    private final FeeTypeRepository feeTypeRepository;
    private final SchoolRepository schoolRepository;
    private final ClassroomRepository classroomRepository;
    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public FeePlanResponse createFeePlan(FeePlanRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        FeeType feeType = feeTypeRepository.findByIdAndSchoolIdAndDeletedFalse(
                request.getFeeTypeId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", request.getFeeTypeId()));

        Classroom classroom = classroomRepository.findByIdAndSchoolId(
                request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", request.getClassroomId()));

        AcademicSession session = sessionRepository.findByIdAndSchoolId(
                request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", request.getSessionId()));

        Term term = termRepository.findByIdAndSchoolId(
                request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", request.getTermId()));

        FeePlan feePlan = FeePlan.builder()
                .school(school)
                .feeType(feeType)
                .classroom(classroom)
                .session(session)
                .term(term)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .isOptional(request.isOptional())
                .description(request.getDescription())
                .build();

        FeePlan saved = feePlanRepository.save(feePlan);
        log.info("Fee plan created: {} for {} {}",
                feeType.getName(), classroom.getName(), classroom.getSection());

        return mapToFeePlanResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FeePlanResponse getFeePlanById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        FeePlan feePlan = feePlanRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeePlan", "id", id));

        return mapToFeePlanResponse(feePlan);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FeePlanResponse> getAllFeePlans(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<FeePlan> feePlanPage = feePlanRepository
                .findAllBySchoolIdAndDeletedFalse(schoolId, PageRequest.of(page, size));

        return PageUtil.from(feePlanPage, this::mapToFeePlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeePlanResponse> getFeePlansByClassroom(Long classroomId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return feePlanRepository.findAllByClassroomIdAndSchoolIdAndDeletedFalse(classroomId, schoolId)
                .stream()
                .map(this::mapToFeePlanResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeePlanResponse> getFeePlansByClassroomAndTerm(
            Long classroomId, Long termId, Long sessionId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return feePlanRepository
                .findAllByClassroomIdAndTermIdAndSessionIdAndSchoolIdAndDeletedFalse(
                        classroomId, termId, sessionId, schoolId)
                .stream()
                .map(this::mapToFeePlanResponse)
                .toList();
    }

    @Override
    @Transactional
    public FeePlanResponse updateFeePlan(Long id, FeePlanRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        FeePlan feePlan = feePlanRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeePlan", "id", id));

        FeeType feeType = feeTypeRepository.findByIdAndSchoolIdAndDeletedFalse(
                request.getFeeTypeId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", request.getFeeTypeId()));

        Classroom classroom = classroomRepository.findByIdAndSchoolId(
                request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", request.getClassroomId()));

        AcademicSession session = sessionRepository.findByIdAndSchoolId(
                request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", request.getSessionId()));

        Term term = termRepository.findByIdAndSchoolId(
                request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", request.getTermId()));

        feePlan.setFeeType(feeType);
        feePlan.setClassroom(classroom);
        feePlan.setSession(session);
        feePlan.setTerm(term);
        feePlan.setAmount(request.getAmount());
        feePlan.setDueDate(request.getDueDate());
        feePlan.setOptional(request.isOptional());
        feePlan.setDescription(request.getDescription());

        FeePlan saved = feePlanRepository.save(feePlan);
        log.info("Fee plan updated: {}", id);

        return mapToFeePlanResponse(saved);
    }

    @Override
    @Transactional
    public void deleteFeePlan(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        FeePlan feePlan = feePlanRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeePlan", "id", id));

        feePlan.setDeleted(true);
        feePlanRepository.save(feePlan);
        log.info("Fee plan deleted: {}", id);
    }

    private FeePlanResponse mapToFeePlanResponse(FeePlan feePlan) {
        FeeTypeResponse feeTypeResponse = FeeTypeResponse.builder()
                .id(feePlan.getFeeType().getId())
                .name(feePlan.getFeeType().getName())
                .category(feePlan.getFeeType().getCategory())
                .description(feePlan.getFeeType().getDescription())
                .active(feePlan.getFeeType().isActive())
                .createdAt(feePlan.getFeeType().getCreatedAt())
                .build();

        return FeePlanResponse.builder()
                .id(feePlan.getId())
                .feeTypeId(feePlan.getFeeType().getId())
                .feeTypeName(feePlan.getFeeType().getName())
                .feeType(feeTypeResponse)
                .classroomId(feePlan.getClassroom().getId())
                .classroomName(feePlan.getClassroom().getName())
                .classroomSection(feePlan.getClassroom().getSection())
                .sessionId(feePlan.getSession().getId())
                .sessionName(feePlan.getSession().getName())
                .termId(feePlan.getTerm().getId())
                .termName(feePlan.getTerm().getName())
                .amount(feePlan.getAmount())
                .dueDate(feePlan.getDueDate())
                .isOptional(feePlan.isOptional())
                .description(feePlan.getDescription())
                .active(feePlan.isActive())
                .createdAt(feePlan.getCreatedAt())
                .build();
    }
}
