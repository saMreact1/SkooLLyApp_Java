package com.samreact.skooLLy.modules.grades.service;

import com.samreact.skooLLy.modules.grades.dto.GradingScaleRequest;
import com.samreact.skooLLy.modules.grades.dto.GradingScaleResponse;
import com.samreact.skooLLy.modules.grades.dto.ReportCardResponse;

import java.util.List;

public interface GradingScaleService {
    GradingScaleResponse createGradingScale(GradingScaleRequest request);

    List<GradingScaleResponse> getGradingScales();

    ReportCardResponse getReportCard(Long studentId, Long termId);

    void deleteGradingScale(Long id);
}
