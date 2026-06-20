package com.samreact.skooLLy.modules.grades.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.grades.dto.BulkGradeRequest;
import com.samreact.skooLLy.modules.grades.dto.GradeRequest;
import com.samreact.skooLLy.modules.grades.dto.GradeResponse;
import com.samreact.skooLLy.modules.grades.dto.GradeSheetResponse;
import com.samreact.skooLLy.modules.grades.entity.Exam;
import com.samreact.skooLLy.modules.grades.entity.Grade;
import com.samreact.skooLLy.modules.grades.entity.GradingScale;
import com.samreact.skooLLy.modules.grades.repository.ExamRepository;
import com.samreact.skooLLy.modules.grades.repository.GradeRepository;
import com.samreact.skooLLy.modules.grades.repository.GradingScaleRepository;
import com.samreact.skooLLy.modules.grades.service.GradeService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final ExamRepository examRepository;
    private final GradingScaleRepository gradingScaleRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public GradeResponse addGrade(GradeRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(request.getExamId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", request.getExamId()));

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(request.getStudentId(), schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        if (request.getMarksObtained().compareTo(BigDecimal.valueOf(exam.getMaxMarks())) > 0) {
            throw new BusinessException("Marks obtained cannot exceed max marks of " + exam.getMaxMarks(),
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        var existing = gradeRepository.findByExamIdAndStudentIdAndDeletedFalse(
                request.getExamId(), request.getStudentId());

        Grade grade;
        if (existing.isPresent()) {
            grade = existing.get();
            grade.setMarksObtained(request.getMarksObtained());
            grade.setRemark(request.getRemark());
        } else {
            grade = Grade.builder()
                    .school(school)
                    .exam(exam)
                    .student(student)
                    .marksObtained(request.getMarksObtained())
                    .remark(request.getRemark())
                    .build();
        }

        String letterGrade = calculateLetterGrade(
                request.getMarksObtained(), BigDecimal.valueOf(exam.getMaxMarks()));
        grade.setLetterGrade(letterGrade);

        Grade saved = gradeRepository.save(grade);
        log.info("Grade saved for student {} in exam {}",
                student.getAdmissionNumber(), exam.getName());

        return mapToGradeResponse(saved);
    }

    @Override
    @Transactional
    public List<GradeResponse> bulkAddGrades(BulkGradeRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(request.getExamId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", request.getExamId()));

        List<GradeResponse> responses = new ArrayList<>();

        for (BulkGradeRequest.StudentGradeRecord record : request.getRecords()) {
            Student student = studentRepository
                    .findByIdAndSchoolIdAndDeleted(record.getStudentId(), schoolId, false)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student", "id", record.getStudentId()));

            if (record.getMarksObtained().compareTo(BigDecimal.valueOf(exam.getMaxMarks())) > 0) {
                throw new BusinessException("Marks obtained for student "
                        + student.getAdmissionNumber() + " cannot exceed max marks of "
                        + exam.getMaxMarks(),
                        org.springframework.http.HttpStatus.BAD_REQUEST);
            }

            var existing = gradeRepository.findByExamIdAndStudentIdAndDeletedFalse(
                    request.getExamId(), record.getStudentId());

            Grade grade;
            if (existing.isPresent()) {
                grade = existing.get();
                grade.setMarksObtained(record.getMarksObtained());
                grade.setRemark(record.getRemark());
            } else {
                grade = Grade.builder()
                        .school(school)
                        .exam(exam)
                        .student(student)
                        .marksObtained(record.getMarksObtained())
                        .remark(record.getRemark())
                        .build();
            }

            String letterGrade = calculateLetterGrade(
                    record.getMarksObtained(), BigDecimal.valueOf(exam.getMaxMarks()));
            grade.setLetterGrade(letterGrade);

            Grade saved = gradeRepository.save(grade);
            responses.add(mapToGradeResponse(saved));
        }

        log.info("Bulk grades saved for {} students in exam {}",
                request.getRecords().size(), exam.getName());

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse getGradeById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Grade grade = gradeRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
        return mapToGradeResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GradeResponse> getGradesByExam(Long examId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        examRepository.findByIdAndSchoolIdAndDeletedFalse(examId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<Grade> allGrades = gradeRepository.findAllByExamIdAndDeletedFalse(examId);
        int start = Math.min(page * size, allGrades.size());
        int end = Math.min(start + size, allGrades.size());
        List<Grade> pageContent = allGrades.subList(start, end);

        List<GradeResponse> responses = pageContent.stream()
                .map(this::mapToGradeResponse)
                .toList();

        return PagedResponse.<GradeResponse>builder()
                .content(responses)
                .page(page)
                .size(size)
                .totalElements(allGrades.size())
                .totalPages((int) Math.ceil((double) allGrades.size() / size))
                .first(page == 0)
                .last(end >= allGrades.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GradeResponse> getGradesByStudent(Long studentId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        studentRepository.findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Grade> allGrades = gradeRepository.findAllByStudentIdAndDeletedFalse(studentId);
        int start = Math.min(page * size, allGrades.size());
        int end = Math.min(start + size, allGrades.size());
        List<Grade> pageContent = allGrades.subList(start, end);

        List<GradeResponse> responses = pageContent.stream()
                .map(this::mapToGradeResponse)
                .toList();

        return PagedResponse.<GradeResponse>builder()
                .content(responses)
                .page(page)
                .size(size)
                .totalElements(allGrades.size())
                .totalPages((int) Math.ceil((double) allGrades.size() / size))
                .first(page == 0)
                .last(end >= allGrades.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GradeSheetResponse getGradeSheet(Long examId) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Exam exam = examRepository.findByIdAndSchoolIdAndDeletedFalse(examId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<Grade> grades = gradeRepository.findAllByExamIdAndDeletedFalse(examId);

        List<GradeSheetResponse.StudentGradeEntry> entries = grades.stream()
                .map(g -> GradeSheetResponse.StudentGradeEntry.builder()
                        .studentId(g.getStudent().getId())
                        .studentName(g.getStudent().getUser().getFirstName()
                                + " " + g.getStudent().getUser().getLastName())
                        .admissionNumber(g.getStudent().getAdmissionNumber())
                        .marksObtained(g.getMarksObtained())
                        .percentage(g.getMarksObtained()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(exam.getMaxMarks()), 1, RoundingMode.HALF_UP))
                        .letterGrade(g.getLetterGrade())
                        .remark(g.getRemark())
                        .build())
                .sorted(Comparator.comparing(
                        GradeSheetResponse.StudentGradeEntry::getMarksObtained).reversed())
                .toList();

        BigDecimal avg = gradeRepository.findAverageByExamId(examId);
        BigDecimal max = gradeRepository.findMaxByExamId(examId);
        BigDecimal min = gradeRepository.findMinByExamId(examId);

        return GradeSheetResponse.builder()
                .examId(exam.getId())
                .examName(exam.getName())
                .examType(exam.getExamType().name())
                .classroomId(exam.getClassroom().getId())
                .classroomName(exam.getClassroom().getName())
                .subjectId(exam.getSubject().getId())
                .subjectName(exam.getSubject().getName())
                .termId(exam.getTerm().getId())
                .termName(exam.getTerm().getName())
                .maxMarks(exam.getMaxMarks())
                .grades(entries)
                .classAverage(avg)
                .highestMark(max)
                .lowestMark(min)
                .build();
    }

    @Override
    @Transactional
    public GradeResponse updateGrade(Long id, GradeRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Grade grade = gradeRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));

        if (request.getMarksObtained() != null) {
            grade.setMarksObtained(request.getMarksObtained());
        }
        if (request.getRemark() != null) {
            grade.setRemark(request.getRemark());
        }

        String letterGrade = calculateLetterGrade(
                grade.getMarksObtained(), BigDecimal.valueOf(grade.getExam().getMaxMarks()));
        grade.setLetterGrade(letterGrade);

        Grade saved = gradeRepository.save(grade);
        log.info("Grade updated for student {} in exam {}",
                saved.getStudent().getAdmissionNumber(), saved.getExam().getName());

        return mapToGradeResponse(saved);
    }

    @Override
    @Transactional
    public void deleteGrade(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Grade grade = gradeRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));

        grade.setDeleted(true);
        gradeRepository.save(grade);
        log.info("Grade deleted: {}", id);
    }

    private String calculateLetterGrade(BigDecimal marksObtained, BigDecimal maxMarks) {
        if (maxMarks.compareTo(BigDecimal.ZERO) == 0) return "F";
        BigDecimal percentage = marksObtained
                .multiply(BigDecimal.valueOf(100))
                .divide(maxMarks, 1, RoundingMode.HALF_UP);

        List<GradingScale> scales = gradingScaleRepository
                .findAllBySchoolIdAndDeletedFalseOrderByMaxPercentageDesc(
                        currentUserService.getCurrentSchoolId());

        if (!scales.isEmpty()) {
            for (GradingScale scale : scales) {
                if (percentage.compareTo(scale.getMinPercentage()) >= 0
                        && percentage.compareTo(scale.getMaxPercentage()) <= 0) {
                    return scale.getGradeLetter();
                }
            }
            return percentage.compareTo(BigDecimal.valueOf(50)) >= 0 ? "C" : "F";
        }

        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) return "A+";
        if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) return "A";
        if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return "B+";
        if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return "B";
        if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return "C";
        if (percentage.compareTo(BigDecimal.valueOf(40)) >= 0) return "D";
        return "F";
    }

    private GradeResponse mapToGradeResponse(Grade grade) {
        Exam exam = grade.getExam();
        BigDecimal percentage = BigDecimal.ZERO;
        if (exam.getMaxMarks() > 0) {
            percentage = grade.getMarksObtained()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(exam.getMaxMarks()), 1, RoundingMode.HALF_UP);
        }

        return GradeResponse.builder()
                .id(grade.getId())
                .examId(exam.getId())
                .examName(exam.getName())
                .examType(exam.getExamType().name())
                .studentId(grade.getStudent().getId())
                .studentName(grade.getStudent().getUser().getFirstName()
                        + " " + grade.getStudent().getUser().getLastName())
                .admissionNumber(grade.getStudent().getAdmissionNumber())
                .marksObtained(grade.getMarksObtained())
                .maxMarks(exam.getMaxMarks())
                .percentage(percentage)
                .letterGrade(grade.getLetterGrade())
                .remark(grade.getRemark())
                .createdAt(grade.getCreatedAt())
                .build();
    }
}
