package com.samreact.skooLLy.modules.fees.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.fees.dto.FeePlanRequest;
import com.samreact.skooLLy.modules.fees.dto.FeePlanResponse;

import java.util.List;

public interface FeePlanService {
    FeePlanResponse createFeePlan(FeePlanRequest request);

    FeePlanResponse getFeePlanById(Long id);

    PagedResponse<FeePlanResponse> getAllFeePlans(int page, int size);

    List<FeePlanResponse> getFeePlansByClassroom(Long classroomId);

    List<FeePlanResponse> getFeePlansByClassroomAndTerm(
            Long classroomId, Long termId, Long sessionId);

    FeePlanResponse updateFeePlan(Long id, FeePlanRequest request);

    void deleteFeePlan(Long id);
}
