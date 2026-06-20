package com.samreact.skooLLy.modules.grades.service.implementation;

import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.repository.TermRepository;
import com.samreact.skooLLy.modules.grades.dto.GradingScaleRequest;
import com.samreact.skooLLy.modules.grades.dto.GradingScaleResponse;
import com.samreact.skooLLy.modules.grades.dto.ReportCardResponse;
import com.samreact.skooLLy.modules.grades.entity.Exam;
import com.samreact.skooLLy.modules.grades.entity.Grade;
import com.samreact.skooLLy.modules.grades.entity.GradingScale;
import com.samreact.skooLLy.modules.grades.repository.ExamRepository;
import com.samreact.skooLLy.modules.grades.repository.GradeRepository;
import com.samreact.skooLLy.modules.grades.repository.GradingScaleRepository;
import com.samreact.skooLLy.modules.grades.service.GradingScaleService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradingScaleServiceImpl implements GradingScaleService {

    private final GradingScaleRepository gradingScaleRepository;
    private final GradeRepository gradeRepository;
    private final ExamRepository examRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final TermRepository termRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public GradingScaleResponse createGradingScale(GradingScaleRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        GradingScale scale = GradingScale.builder()
                .school(school)
                .gradeLetter(request.getGradeLetter())
                .minPercentage(request.getMinPercentage())
                .maxPercentage(request.getMaxPercentage())
                .description(request.getDescription())
                .build();

        GradingScale saved = gradingScaleRepository.save(scale);
        log.info("Grading scale created: {}", saved.getGradeLetter());

        return mapToGradingScaleResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradingScaleResponse> getGradingScales() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        return gradingScaleRepository.findAllBySchoolIdAndDeletedFalseOrderByMaxPercentageDesc(schoolId)
                .stream()
                .map(this::mapToGradingScaleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportCardResponse getReportCard(Long studentId, Long termId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", termId));

        List<Exam> termExams = examRepository.findAllByTermIdAndSchoolIdAndDeletedFalse(termId, schoolId);

        List<Grade> studentGrades = gradeRepository.findAllByStudentIdAndDeletedFalse(studentId);

        Map<Long, List<Grade>> gradesBySubject = studentGrades.stream()
                .filter(g -> termExams.stream().anyMatch(e -> e.getId().equals(g.getExam().getId())))
                .collect(Collectors.groupingBy(g -> g.getExam().getSubject().getId()));

        List<ReportCardResponse.SubjectResult> results = new ArrayList<>();
        BigDecimal totalObtained = BigDecimal.ZERO;
        BigDecimal totalMax = BigDecimal.ZERO;

        for (Map.Entry<Long, List<Grade>> entry : gradesBySubject.entrySet()) {
            List<Grade> subjectGrades = entry.getValue();
            if (subjectGrades.isEmpty()) continue;

            String subjectName = subjectGrades.get(0).getExam().getSubject().getName();
            BigDecimal marksObtained = subjectGrades.stream()
                    .map(Grade::getMarksObtained)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int maxMarks = subjectGrades.stream()
                    .mapToInt(g -> g.getExam().getMaxMarks())
                    .sum();

            BigDecimal percentage = maxMarks > 0
                    ? marksObtained.multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(maxMarks), 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            String letterGrade = calculateLetterGrade(percentage);

            totalObtained = totalObtained.add(marksObtained);
            totalMax = totalMax.add(BigDecimal.valueOf(maxMarks));

            results.add(ReportCardResponse.SubjectResult.builder()
                    .subjectId(entry.getKey())
                    .subjectName(subjectName)
                    .marksObtained(marksObtained)
                    .maxMarks(maxMarks)
                    .percentage(percentage)
                    .letterGrade(letterGrade)
                    .examCount(subjectGrades.size())
                    .build());
        }

        BigDecimal overallPercentage = totalMax.compareTo(BigDecimal.ZERO) > 0
                ? totalObtained.multiply(BigDecimal.valueOf(100))
                .divide(totalMax, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ReportCardResponse.builder()
                .studentId(studentId)
                .studentName(student.getUser().getFirstName()
                        + " " + student.getUser().getLastName())
                .admissionNumber(student.getAdmissionNumber())
                .classroomName(student.getCurrentClass())
                .sessionName(term.getSession().getName())
                .termName(term.getName())
                .results(results)
                .totalMarksObtained(totalObtained)
                .totalMaxMarks(totalMax)
                .overallPercentage(overallPercentage)
                .overallGrade(calculateLetterGrade(overallPercentage))
                .rank(0)
                .build();
    }

    @Override
    @Transactional
    public void deleteGradingScale(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        GradingScale scale = gradingScaleRepository.findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Grading Scale", "id", id));

        scale.setDeleted(true);
        gradingScaleRepository.save(scale);
        log.info("Grading scale deleted: {}", id);
    }

    private String calculateLetterGrade(BigDecimal percentage) {
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

    private GradingScaleResponse mapToGradingScaleResponse(GradingScale scale) {
        return GradingScaleResponse.builder()
                .id(scale.getId())
                .gradeLetter(scale.getGradeLetter())
                .minPercentage(scale.getMinPercentage())
                .maxPercentage(scale.getMaxPercentage())
                .description(scale.getDescription())
                .build();
    }
}
