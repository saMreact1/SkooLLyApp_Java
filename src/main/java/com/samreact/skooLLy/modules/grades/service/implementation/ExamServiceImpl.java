package com.samreact.skooLLy.modules.grades.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.academic.entity.AcademicSession;
import com.samreact.skooLLy.modules.academic.entity.Classroom;
import com.samreact.skooLLy.modules.academic.entity.Subject;
import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.repository.AcademicSessionRepository;
import com.samreact.skooLLy.modules.academic.repository.ClassroomRepository;
import com.samreact.skooLLy.modules.academic.repository.SubjectRepository;
import com.samreact.skooLLy.modules.academic.repository.TermRepository;
import com.samreact.skooLLy.modules.grades.dto.ExamRequest;
import com.samreact.skooLLy.modules.grades.dto.ExamResponse;
import com.samreact.skooLLy.modules.grades.entity.Exam;
import com.samreact.skooLLy.modules.grades.repository.ExamRepository;
import com.samreact.skooLLy.modules.grades.service.ExamService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final SchoolRepository schoolRepository;
    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Classroom classroom = classroomRepository.findByIdAndSchoolId(request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", request.getClassroomId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        AcademicSession session = sessionRepository.findByIdAndSchoolId(request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", request.getSessionId()));

        Term term = termRepository.findByIdAndSchoolId(request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", request.getTermId()));

        Exam exam = Exam.builder()
                .school(school)
                .name(request.getName())
                .examType(request.getExamType())
                .classroom(classroom)
                .subject(subject)
                .session(session)
                .term(term)
                .examDate(request.getExamDate())
                .maxMarks(request.getMaxMarks())
                .weightage(request.getWeightage() != null ? request.getWeightage() : 0)
                .description(request.getDescription())
                .build();

        Exam saved = examRepository.save(exam);
        log.info("Exam created: {} for classroom {} subject {}",
                saved.getName(), classroom.getName(), subject.getName());

        return mapToExamResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        return mapToExamResponse(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExamResponse> getExams(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Page<Exam> examPage = examRepository.findAllBySchoolIdAndDeletedFalse(
                schoolId, PageRequest.of(page, size));
        return PageUtil.from(examPage, this::mapToExamResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExamResponse> getExamsByClassroom(Long classroomId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Page<Exam> examPage = examRepository.findAllByClassroomIdAndSchoolIdAndDeletedFalse(
                classroomId, schoolId, PageRequest.of(page, size));
        return PageUtil.from(examPage, this::mapToExamResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExamResponse> getExamsBySubject(Long subjectId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Page<Exam> examPage = examRepository.findAllBySubjectIdAndSchoolIdAndDeletedFalse(
                subjectId, schoolId, PageRequest.of(page, size));
        return PageUtil.from(examPage, this::mapToExamResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExamResponse> getExamsByTerm(Long termId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Page<Exam> examPage = examRepository.findAllByTermIdAndSchoolIdAndDeletedFalse(
                termId, schoolId, PageRequest.of(page, size));
        return PageUtil.from(examPage, this::mapToExamResponse);
    }

    @Override
    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        Classroom classroom = classroomRepository.findByIdAndSchoolId(request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", request.getClassroomId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        Term term = termRepository.findByIdAndSchoolId(request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", request.getTermId()));

        exam.setName(request.getName());
        exam.setExamType(request.getExamType());
        exam.setClassroom(classroom);
        exam.setSubject(subject);
        exam.setTerm(term);
        exam.setExamDate(request.getExamDate());
        exam.setMaxMarks(request.getMaxMarks());
        exam.setWeightage(request.getWeightage() != null ? request.getWeightage() : 0);
        exam.setDescription(request.getDescription());

        Exam saved = examRepository.save(exam);
        log.info("Exam updated: {}", saved.getName());

        return mapToExamResponse(saved);
    }

    @Override
    @Transactional
    public ExamResponse publishExam(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        exam.setPublished(true);
        Exam saved = examRepository.save(exam);
        log.info("Exam published: {}", saved.getName());

        return mapToExamResponse(saved);
    }

    @Override
    @Transactional
    public void deleteExam(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        exam.setDeleted(true);
        examRepository.save(exam);
        log.info("Exam deleted: {}", id);
    }

    private ExamResponse mapToExamResponse(Exam exam) {
        long totalGrades = examRepository.countGradesByExamId(exam.getId());
        return ExamResponse.builder()
                .id(exam.getId())
                .name(exam.getName())
                .examType(exam.getExamType())
                .classroomId(exam.getClassroom().getId())
                .classroomName(exam.getClassroom().getName())
                .classroomSection(exam.getClassroom().getSection())
                .subjectId(exam.getSubject().getId())
                .subjectName(exam.getSubject().getName())
                .sessionId(exam.getSession().getId())
                .sessionName(exam.getSession().getName())
                .termId(exam.getTerm().getId())
                .termName(exam.getTerm().getName())
                .examDate(exam.getExamDate())
                .maxMarks(exam.getMaxMarks())
                .weightage(exam.getWeightage())
                .description(exam.getDescription())
                .published(exam.isPublished())
                .totalGrades((int) totalGrades)
                .createdAt(exam.getCreatedAt())
                .build();
    }
}
