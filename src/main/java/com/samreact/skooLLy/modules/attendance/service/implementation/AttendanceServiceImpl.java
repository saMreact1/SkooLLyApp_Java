package com.samreact.skooLLy.modules.attendance.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.academic.entity.AcademicSession;
import com.samreact.skooLLy.modules.academic.entity.Classroom;
import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.repository.AcademicSessionRepository;
import com.samreact.skooLLy.modules.academic.repository.ClassroomRepository;
import com.samreact.skooLLy.modules.academic.repository.TermRepository;
import com.samreact.skooLLy.modules.attendance.dto.AttendanceResponse;
import com.samreact.skooLLy.modules.attendance.dto.AttendanceSummaryResponse;
import com.samreact.skooLLy.modules.attendance.dto.BulkMarkAttendanceRequest;
import com.samreact.skooLLy.modules.attendance.dto.MarkAttendanceRequest;
import com.samreact.skooLLy.modules.attendance.entity.Attendance;
import com.samreact.skooLLy.modules.attendance.entity.enums.AttendanceStatus;
import com.samreact.skooLLy.modules.attendance.repository.AttendanceRepository;
import com.samreact.skooLLy.modules.attendance.service.AttendanceService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.repository.StudentRepository;
import com.samreact.skooLLy.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(MarkAttendanceRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(request.getStudentId(), schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", request.getStudentId()));

        Classroom classroom = classroomRepository
                .findByIdAndSchoolId(request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", request.getClassroomId()));

        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", request.getSessionId()));

        Term term = termRepository
                .findByIdAndSchoolId(request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", request.getTermId()));

        User markedBy = currentUserService.getCurrentUser();

        Optional<Attendance> existing = attendanceRepository
                .findByStudentIdAndDateAndDeletedFalse(student.getId(), request.getDate());

        if (existing.isPresent()) {
            Attendance record = existing.get();
            record.setStatus(request.getStatus());
            record.setCheckInTime(request.getCheckInTime());
            record.setCheckOutTime(request.getCheckOutTime());
            record.setRemark(request.getRemark());
            record.setMarkedBy(markedBy);
            Attendance saved = attendanceRepository.save(record);
            log.info("Attendance updated for student {} on date {}: {}",
                    student.getAdmissionNumber(), request.getDate(), request.getStatus());
            return mapToAttendanceResponse(saved);
        }

        Attendance attendance = Attendance.builder()
                .school(school)
                .student(student)
                .classroom(classroom)
                .session(session)
                .term(term)
                .date(request.getDate())
                .status(request.getStatus())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .remark(request.getRemark())
                .markedBy(markedBy)
                .build();

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance marked for student {} on date {}: {}",
                student.getAdmissionNumber(), request.getDate(), request.getStatus());

        return mapToAttendanceResponse(saved);
    }

    @Override
    @Transactional
    public List<AttendanceResponse> bulkMarkAttendance(BulkMarkAttendanceRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        Classroom classroom = classroomRepository
                .findByIdAndSchoolId(request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", request.getClassroomId()));

        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", request.getSessionId()));

        Term term = termRepository
                .findByIdAndSchoolId(request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", request.getTermId()));

        User markedBy = currentUserService.getCurrentUser();
        List<AttendanceResponse> responses = new ArrayList<>();

        for (BulkMarkAttendanceRequest.StudentAttendanceRecord record : request.getRecords()) {
            Student student = studentRepository
                    .findByIdAndSchoolIdAndDeleted(record.getStudentId(), schoolId, false)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student", "id", record.getStudentId()));

            Optional<Attendance> existing = attendanceRepository
                    .findByStudentIdAndDateAndDeletedFalse(student.getId(), request.getDate());

            if (existing.isPresent()) {
                Attendance att = existing.get();
                att.setStatus(record.getStatus());
                att.setCheckInTime(record.getCheckInTime());
                att.setCheckOutTime(record.getCheckOutTime());
                att.setRemark(record.getRemark());
                att.setMarkedBy(markedBy);
                Attendance saved = attendanceRepository.save(att);
                responses.add(mapToAttendanceResponse(saved));
            } else {
                Attendance attendance = Attendance.builder()
                        .school(school)
                        .student(student)
                        .classroom(classroom)
                        .session(session)
                        .term(term)
                        .date(request.getDate())
                        .status(record.getStatus())
                        .checkInTime(record.getCheckInTime())
                        .checkOutTime(record.getCheckOutTime())
                        .remark(record.getRemark())
                        .markedBy(markedBy)
                        .build();

                Attendance saved = attendanceRepository.save(attendance);
                responses.add(mapToAttendanceResponse(saved));
            }
        }

        log.info("Bulk attendance marked for {} students on date {}",
                request.getRecords().size(), request.getDate());

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Attendance attendance = attendanceRepository
                .findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance", "id", id));

        return mapToAttendanceResponse(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AttendanceResponse> getAttendanceByStudent(
            Long studentId, Long termId, int page, int size) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        studentRepository.findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", studentId));

        Page<Attendance> attendancePage = attendanceRepository
                .findAllByStudentIdAndTermIdAndDeletedFalse(
                        studentId, termId, PageRequest.of(page, size));

        return PageUtil.from(attendancePage, this::mapToAttendanceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AttendanceResponse> getAttendanceByClassroom(
            Long classroomId, LocalDate date, int page, int size) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        classroomRepository.findByIdAndSchoolId(classroomId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", classroomId));

        Page<Attendance> attendancePage = attendanceRepository
                .findAllByClassroomIdAndDateAndDeletedFalse(
                        classroomId, date, PageRequest.of(page, size));

        return PageUtil.from(attendancePage, this::mapToAttendanceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByClassroomAndDateRange(
            Long classroomId, LocalDate from, LocalDate to) {

        return attendanceRepository
                .findAllByClassroomIdAndDateBetweenAndDeletedFalse(classroomId, from, to)
                .stream()
                .map(this::mapToAttendanceResponse)
                .toList();
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(Long id, MarkAttendanceRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Attendance attendance = attendanceRepository
                .findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance", "id", id));

        User markedBy = currentUserService.getCurrentUser();

        if (request.getStatus() != null) {
            attendance.setStatus(request.getStatus());
        }
        if (request.getCheckInTime() != null) {
            attendance.setCheckInTime(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            attendance.setCheckOutTime(request.getCheckOutTime());
        }
        if (request.getRemark() != null) {
            attendance.setRemark(request.getRemark());
        }
        attendance.setMarkedBy(markedBy);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance updated for student {} on date {}",
                saved.getStudent().getAdmissionNumber(), saved.getDate());

        return mapToAttendanceResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAttendance(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Attendance attendance = attendanceRepository
                .findByIdAndSchoolIdAndDeletedFalse(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance", "id", id));

        attendance.setDeleted(true);
        attendanceRepository.save(attendance);
        log.info("Attendance deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getStudentSummary(Long studentId, Long termId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        studentRepository.findByIdAndSchoolIdAndDeleted(studentId, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", studentId));

        long totalDays = attendanceRepository
                .countByStudentIdAndTermIdAndDeletedFalse(studentId, termId);

        long presentDays = attendanceRepository
                .countByStudentIdAndTermIdAndStatusAndDeletedFalse(
                        studentId, termId, AttendanceStatus.PRESENT);

        long absentDays = attendanceRepository
                .countByStudentIdAndTermIdAndStatusAndDeletedFalse(
                        studentId, termId, AttendanceStatus.ABSENT);

        long lateDays = attendanceRepository
                .countByStudentIdAndTermIdAndStatusAndDeletedFalse(
                        studentId, termId, AttendanceStatus.LATE);

        long excusedDays = attendanceRepository
                .countByStudentIdAndTermIdAndStatusAndDeletedFalse(
                        studentId, termId, AttendanceStatus.EXCUSED);

        long halfDayDays = attendanceRepository
                .countByStudentIdAndTermIdAndStatusAndDeletedFalse(
                        studentId, termId, AttendanceStatus.HALF_DAY);

        double percentage = totalDays > 0
                ? ((double) (presentDays + lateDays + halfDayDays) / totalDays) * 100.0
                : 0.0;

        return AttendanceSummaryResponse.builder()
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .excusedDays(excusedDays)
                .halfDayDays(halfDayDays)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getClassroomSummary(Long classroomId, Long termId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        classroomRepository.findByIdAndSchoolId(classroomId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", classroomId));

        List<Attendance> records = attendanceRepository
                .findAllByClassroomIdAndTermIdAndDeletedFalse(classroomId, termId);

        long totalDays = records.size();
        long presentDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
        long absentDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();
        long lateDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LATE)
                .count();
        long excusedDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.EXCUSED)
                .count();
        long halfDayDays = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY)
                .count();

        double percentage = totalDays > 0
                ? ((double) (presentDays + lateDays + halfDayDays) / totalDays) * 100.0
                : 0.0;

        return AttendanceSummaryResponse.builder()
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .excusedDays(excusedDays)
                .halfDayDays(halfDayDays)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getUser().getFirstName()
                        + " " + attendance.getStudent().getUser().getLastName())
                .admissionNumber(attendance.getStudent().getAdmissionNumber())
                .classroomId(attendance.getClassroom().getId())
                .classroomName(attendance.getClassroom().getName())
                .classroomSection(attendance.getClassroom().getSection())
                .sessionId(attendance.getSession().getId())
                .sessionName(attendance.getSession().getName())
                .termId(attendance.getTerm().getId())
                .termName(attendance.getTerm().getName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .remark(attendance.getRemark())
                .markedByName(attendance.getMarkedBy().getFirstName()
                        + " " + attendance.getMarkedBy().getLastName())
                .createdAt(attendance.getCreatedAt())
                .build();
    }
}
