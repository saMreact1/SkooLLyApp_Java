package com.samreact.skooLLy.modules.grades.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.grades.dto.ExamRequest;
import com.samreact.skooLLy.modules.grades.dto.ExamResponse;

public interface ExamService {
    ExamResponse createExam(ExamRequest request);

    ExamResponse getExamById(Long id);

    PagedResponse<ExamResponse> getExams(int page, int size);

    PagedResponse<ExamResponse> getExamsByClassroom(Long classroomId, int page, int size);

    PagedResponse<ExamResponse> getExamsBySubject(Long subjectId, int page, int size);

    PagedResponse<ExamResponse> getExamsByTerm(Long termId, int page, int size);

    ExamResponse updateExam(Long id, ExamRequest request);

    ExamResponse publishExam(Long id);

    void deleteExam(Long id);
}
