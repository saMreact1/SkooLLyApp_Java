package com.samreact.skooLLy.modules.fees.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeRequest;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeResponse;
import com.samreact.skooLLy.modules.fees.entity.FeeType;
import com.samreact.skooLLy.modules.fees.repository.FeeTypeRepository;
import com.samreact.skooLLy.modules.fees.service.FeeTypeService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeTypeServiceImpl implements FeeTypeService {

    private final FeeTypeRepository feeTypeRepository;
    private final SchoolRepository schoolRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public FeeTypeResponse createFeeType(FeeTypeRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        if (feeTypeRepository.existsByNameAndSchoolIdAndDeletedFalse(
                request.getName(), schoolId)) {
            throw new BusinessException(
                    "Fee type with name '" + request.getName() + "' already exists",
                    HttpStatus.CONFLICT);
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        FeeType feeType = FeeType.builder()
                .school(school)
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();

        FeeType saved = feeTypeRepository.save(feeType);
        log.info("Fee type created: {} ({})", saved.getName(), saved.getCategory());

        return mapToFeeTypeResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FeeTypeResponse getFeeTypeById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        FeeType feeType = feeTypeRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", id));

        return mapToFeeTypeResponse(feeType);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FeeTypeResponse> getAllFeeTypes(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<FeeType> feeTypePage = feeTypeRepository
                .findAllBySchoolIdAndDeletedFalse(schoolId, PageRequest.of(page, size));

        return PageUtil.from(feeTypePage, this::mapToFeeTypeResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeTypeResponse> getAllActiveFeeTypes() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return feeTypeRepository.findAllBySchoolIdAndDeletedFalseAndActiveTrue(schoolId)
                .stream()
                .map(this::mapToFeeTypeResponse)
                .toList();
    }

    @Override
    @Transactional
    public FeeTypeResponse updateFeeType(Long id, FeeTypeRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        FeeType feeType = feeTypeRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", id));

        feeType.setName(request.getName());
        feeType.setCategory(request.getCategory());
        feeType.setDescription(request.getDescription());

        FeeType saved = feeTypeRepository.save(feeType);
        log.info("Fee type updated: {}", saved.getName());

        return mapToFeeTypeResponse(saved);
    }

    @Override
    @Transactional
    public void deleteFeeType(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        FeeType feeType = feeTypeRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", id));

        feeType.setDeleted(true);
        feeTypeRepository.save(feeType);
        log.info("Fee type deleted: {}", id);
    }

    private FeeTypeResponse mapToFeeTypeResponse(FeeType feeType) {
        return FeeTypeResponse.builder()
                .id(feeType.getId())
                .name(feeType.getName())
                .category(feeType.getCategory())
                .description(feeType.getDescription())
                .active(feeType.isActive())
                .createdAt(feeType.getCreatedAt())
                .build();
    }
}
