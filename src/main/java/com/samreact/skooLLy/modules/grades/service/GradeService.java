package com.samreact.skooLLy.modules.grades.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.grades.dto.BulkGradeRequest;
import com.samreact.skooLLy.modules.grades.dto.GradeRequest;
import com.samreact.skooLLy.modules.grades.dto.GradeResponse;
import com.samreact.skooLLy.modules.grades.dto.GradeSheetResponse;

import java.util.List;

public interface GradeService {
    GradeResponse addGrade(GradeRequest request);

    List<GradeResponse> bulkAddGrades(BulkGradeRequest request);

    GradeResponse getGradeById(Long id);

    PagedResponse<GradeResponse> getGradesByExam(Long examId, int page, int size);

    PagedResponse<GradeResponse> getGradesByStudent(Long studentId, int page, int size);

    GradeSheetResponse getGradeSheet(Long examId);

    GradeResponse updateGrade(Long id, GradeRequest request);

    void deleteGrade(Long id);
}
