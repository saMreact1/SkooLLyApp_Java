package com.samreact.skooLLy.modules.fees.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeRequest;
import com.samreact.skooLLy.modules.fees.dto.FeeTypeResponse;

import java.util.List;

public interface FeeTypeService {
    FeeTypeResponse createFeeType(FeeTypeRequest request);

    FeeTypeResponse getFeeTypeById(Long id);

    PagedResponse<FeeTypeResponse> getAllFeeTypes(int page, int size);

    List<FeeTypeResponse> getAllActiveFeeTypes();

    FeeTypeResponse updateFeeType(Long id, FeeTypeRequest request);

    void deleteFeeType(Long id);
}
