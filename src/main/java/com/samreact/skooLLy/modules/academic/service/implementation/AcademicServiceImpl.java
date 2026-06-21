package com.samreact.skooLLy.modules.academic.service.implementation;

import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.DuplicateResourceException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.academic.dto.*;
import com.samreact.skooLLy.modules.academic.entity.*;
import com.samreact.skooLLy.modules.academic.entity.enums.SessionStatus;
import com.samreact.skooLLy.modules.academic.entity.enums.TermStatus;
import com.samreact.skooLLy.modules.academic.repository.*;
import com.samreact.skooLLy.modules.academic.service.AcademicService;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.teacher.entity.Teacher;
import com.samreact.skooLLy.modules.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcademicServiceImpl implements AcademicService {

    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;
    private final SchoolRepository schoolRepository;
    private final CurrentUserService currentUserService;
    private final SubjectRepository subjectRepository;
    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final TimetableRepository timetableRepository;

    @Override
    @Transactional
    public SessionResponse createSession(
            CreateSessionRequest request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", schoolId));

        // Check session name is unique within this school
        if (sessionRepository.existsByNameAndSchoolId(
                request.getName(), schoolId)) {
            throw new DuplicateResourceException(
                    "Session", "name", request.getName());
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException(
                    "End date cannot be before start date",
                    HttpStatus.BAD_REQUEST);
        }

        AcademicSession session = AcademicSession.builder()
                .school(school)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        AcademicSession saved = sessionRepository.save(session);
        log.info("Session created: {} for school: {}",
                saved.getName(), school.getName());

        return mapToSessionResponse(saved);
    }

    @Override
    @Transactional
    public SessionResponse getSessionById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", id));

        return mapToSessionResponse(session);
    }

    @Override
    @Transactional
    public List<SessionResponse> getAllSessions() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return sessionRepository
                .findAllBySchoolId(schoolId)
                .stream()
                .map(this::mapToSessionResponse)
                .toList();
    }

    @Override
    @Transactional
    public SessionResponse setCurrentSession(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        // Remove current flag from existing current session
        sessionRepository
                .findBySchoolIdAndCurrentTrue(schoolId)
                .ifPresent(existing -> {
                    existing.setCurrent(false);
                    existing.setStatus(SessionStatus.COMPLETED);
                    sessionRepository.save(existing);
                });

        // Set new current session
        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", id));

        session.setCurrent(true);
        session.setStatus(SessionStatus.ACTIVE);
        AcademicSession saved = sessionRepository.save(session);

        log.info("Current session set to: {}", saved.getName());
        return mapToSessionResponse(saved);
    }

    @Override
    @Transactional
    public void deleteSession(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", id));

        if (session.getCurrent()) {
            throw new BusinessException(
                    "Cannot delete the current active session",
                    HttpStatus.BAD_REQUEST);
        }

        sessionRepository.delete(session);
        log.info("Session deleted: {}", session.getName());
    }

    @Override
    @Transactional
    public SessionResponse getCurrentSession() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        AcademicSession session = sessionRepository
                .findBySchoolIdAndCurrentTrue(schoolId)
                .orElseThrow(() -> new BusinessException(
                        "No active session found. "
                                + "Please set a current session.",
                        HttpStatus.NOT_FOUND));

        return mapToSessionResponse(session);
    }

    @Override
    @Transactional
    public TermResponse createTerm(CreateTermRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", schoolId));

        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", request.getSessionId()));

        // Check term name is unique within this session
        if (termRepository.existsByNameAndSessionId(
                request.getName(), request.getSessionId())) {
            throw new DuplicateResourceException(
                    "Term", "name", request.getName());
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException(
                    "End date cannot be before start date",
                    HttpStatus.BAD_REQUEST);
        }

        Term term = Term.builder()
                .school(school)
                .session(session)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        Term saved = termRepository.save(term);
        log.info("Term created: {} in session: {}",
                saved.getName(), session.getName());

        return mapToTermResponse(saved);
    }

    @Override
    @Transactional
    public TermResponse getTermById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Term term = termRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", id));

        return mapToTermResponse(term);
    }

    @Override
    @Transactional
    public List<TermResponse> getTermsBySession(Long sessionId) {
        return termRepository
                .findAllBySessionId(sessionId)
                .stream()
                .map(this::mapToTermResponse)
                .toList();
    }

    @Override
    @Transactional
    public TermResponse updateTerm(Long id, UpdateTermRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Term term = termRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", id));

        // Check name uniqueness within session (excluding this term)
        List<Term> existingTerms = termRepository.findAllBySessionId(term.getSession().getId());
        boolean nameTaken = existingTerms.stream()
                .anyMatch(t -> !t.getId().equals(id)
                        && t.getName().equalsIgnoreCase(request.getName()));
        if (nameTaken) {
            throw new DuplicateResourceException(
                    "Term", "name", request.getName());
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException(
                    "End date cannot be before start date",
                    HttpStatus.BAD_REQUEST);
        }

        term.setName(request.getName());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());
        term.setNeedsDateUpdate(false);

        Term saved = termRepository.save(term);
        log.info("Term updated: {} (id: {})", saved.getName(), id);

        return mapToTermResponse(saved);
    }

    @Override
    @Transactional
    public TermResponse setCurrentTerm(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        // Remove current flag from existing current term
        termRepository
                .findBySchoolIdAndCurrentTrue(schoolId)
                .ifPresent(existing -> {
                    existing.setCurrent(false);
                    existing.setStatus(TermStatus.COMPLETED);
                    termRepository.save(existing);
                });

        // Set new current term
        Term term = termRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", id));

        term.setCurrent(true);
        term.setStatus(TermStatus.ACTIVE);
        Term saved = termRepository.save(term);

        log.info("Current term set to: {}", saved.getName());
        return mapToTermResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTerm(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Term term = termRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", id));

        if (term.isCurrent()) {
            throw new BusinessException(
                    "Cannot delete the current active term",
                    HttpStatus.BAD_REQUEST);
        }

        termRepository.delete(term);
        log.info("Term deleted: {}", term.getName());
    }

    @Override
    @Transactional
    public TermResponse getCurrentTerm() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Term term = termRepository
                .findBySchoolIdAndCurrentTrue(schoolId)
                .orElseThrow(() -> new BusinessException(
                        "No active term found. "
                                + "Please set a current term.",
                        HttpStatus.NOT_FOUND));

        return mapToTermResponse(term);
    }

    private SessionResponse mapToSessionResponse(
            AcademicSession session) {

        List<TermResponse> terms = session.getTerms()
                .stream()
                .map(this::mapToTermResponse)
                .toList();

        return SessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .status(session.getStatus())
                .isCurrent(session.getCurrent())
                .needsDateUpdate(session.getNeedsDateUpdate())
                .schoolName(session.getSchool().getName())
                .terms(terms)
                .createdAt(session.getCreatedAt())
                .build();
    }

    // Subject Operations
    @Override
    @Transactional
    public SubjectResponse createSubject(
            CreateSubjectRequest request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", schoolId));

        // Check name is unique within this school
        if (subjectRepository.existsByNameAndSchoolId(
                request.getName(), schoolId)) {
            throw new DuplicateResourceException(
                    "Subject", "name", request.getName());
        }

        // Check code is unique within this school
        if (subjectRepository.existsByCodeAndSchoolId(
                request.getCode(), schoolId)) {
            throw new DuplicateResourceException(
                    "Subject", "code", request.getCode());
        }

        Subject subject = Subject.builder()
                .school(school)
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .category(request.getCategory())
                .isElective(request.isElective())
                .build();

        Subject saved = subjectRepository.save(subject);
        log.info("Subject created: {} - {} at school: {}",
                saved.getCode(), saved.getName(), school.getName());

        return mapToSubjectResponse(saved);
    }

    @Override
    @Transactional
    public SubjectResponse getSubjectById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Subject subject = subjectRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject", "id", id));

        return mapToSubjectResponse(subject);
    }

    @Override
    @Transactional
    public List<SubjectResponse> getAllSubjects() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return subjectRepository
                .findAllBySchoolId(schoolId)
                .stream()
                .map(this::mapToSubjectResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<SubjectResponse> getElectiveSubjects() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return subjectRepository
                .findAllBySchoolIdAndIsElective(schoolId, true)
                .stream()
                .map(this::mapToSubjectResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(Long id,
                                         UpdateSubjectRequest request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        Subject subject = subjectRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject", "id", id));

        if (request.getName() != null) {
            subject.setName(request.getName());
        }
        if (request.getCode() != null) {
            subject.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            subject.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            subject.setCategory(request.getCategory());
        }
        if (request.getIsElective() != null) {
            subject.setElective(request.getIsElective());
        }
        if (request.getActive() != null) {
            subject.setActive(request.getActive());
        }

        Subject updated = subjectRepository.save(subject);
        log.info("Subject updated: {}", updated.getName());

        return mapToSubjectResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Subject subject = subjectRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject", "id", id));

        subjectRepository.delete(subject);
        log.info("Subject deleted: {}", subject.getName());
    }

    // ── Classroom Operations ──────────────────────────────────

    @Override
    @Transactional
    public ClassroomResponse createClassroom(
            CreateClassroomRequest request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", schoolId));

        // Check name + section is unique within this school
        if (classroomRepository.existsByNameAndSectionAndSchoolId(
                request.getName(), request.getSection(), schoolId)) {
            throw new DuplicateResourceException(
                    "Classroom",
                    "name and section",
                    request.getName() + " " + request.getSection());
        }

        // Load class teacher if provided
        Teacher classTeacher = null;
        if (request.getClassTeacherId() != null) {
            classTeacher = teacherRepository
                    .findByIdAndSchoolId(
                            request.getClassTeacherId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Teacher", "id",
                            request.getClassTeacherId()));
        }

        Classroom classroom = Classroom.builder()
                .school(school)
                .name(request.getName())
                .section(request.getSection())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .classTeacher(classTeacher)
                .level(request.getLevel())
                .build();

        Classroom saved = classroomRepository.save(classroom);
        log.info("Classroom created: {} {} at school: {}",
                saved.getName(), saved.getSection(),
                school.getName());

        return mapToClassroomResponse(saved);
    }

    @Override
    @Transactional
    public ClassroomResponse getClassroomById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Classroom classroom = classroomRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", id));

        return mapToClassroomResponse(classroom);
    }

    @Override
    @Transactional
    public List<ClassroomResponse> getAllClassrooms() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return classroomRepository
                .findAllBySchoolId(schoolId)
                .stream()
                .map(this::mapToClassroomResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<ClassroomResponse> getClassroomsByLevel(
            String level) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        return classroomRepository
                .findAllBySchoolIdAndLevel(schoolId, level)
                .stream()
                .map(this::mapToClassroomResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClassroomResponse updateClassroom(Long id,
                                             UpdateClassroomRequest request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        Classroom classroom = classroomRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", id));

        if (request.getName() != null) {
            classroom.setName(request.getName());
        }
        if (request.getSection() != null) {
            classroom.setSection(request.getSection());
        }
        if (request.getDescription() != null) {
            classroom.setDescription(request.getDescription());
        }
        if (request.getCapacity() != null) {
            classroom.setCapacity(request.getCapacity());
        }
        if (request.getLevel() != null) {
            classroom.setLevel(request.getLevel());
        }
        if (request.getActive() != null) {
            classroom.setActive(request.getActive());
        }
        if (request.getClassTeacherId() != null) {
            Teacher teacher = teacherRepository
                    .findByIdAndSchoolId(
                            request.getClassTeacherId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Teacher", "id",
                            request.getClassTeacherId()));
            classroom.setClassTeacher(teacher);
        }

        Classroom updated = classroomRepository.save(classroom);
        log.info("Classroom updated: {} {}",
                updated.getName(), updated.getSection());

        return mapToClassroomResponse(updated);
    }

    @Override
    @Transactional
    public void deleteClassroom(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Classroom classroom = classroomRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", id));

        classroomRepository.delete(classroom);
        log.info("Classroom deleted: {} {}",
                classroom.getName(), classroom.getSection());
    }

    @Override
    @Transactional
    public long getClassroomCount() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        return classroomRepository.countBySchoolId(schoolId);
    }

    @Override
    @Transactional
    public TimetableResponse createTimetableEntry(
            CreateTimetableRequest request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", schoolId));

        AcademicSession session = sessionRepository
                .findByIdAndSchoolId(request.getSessionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", request.getSessionId()));

        Term term = termRepository
                .findByIdAndSchoolId(request.getTermId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Term", "id", request.getTermId()));

        Classroom classroom = classroomRepository
                .findByIdAndSchoolId(request.getClassroomId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classroom", "id", request.getClassroomId()));

        Subject subject = subjectRepository
                .findByIdAndSchoolId(request.getSubjectId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject", "id", request.getSubjectId()));

        Teacher teacher = teacherRepository
                .findByIdAndSchoolId(request.getTeacherId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher", "id", request.getTeacherId()));

        // Check for classroom scheduling conflict
        if (timetableRepository
                .existsByClassroomIdAndTermIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        request.getClassroomId(),
                        request.getTermId(),
                        request.getDayOfWeek(),
                        request.getEndTime(),
                        request.getStartTime())) {
            throw new BusinessException(
                    "Classroom already has a class scheduled "
                            + "at this time",
                    HttpStatus.CONFLICT);
        }

        // Check for teacher scheduling conflict
        if (timetableRepository
                .existsByTeacherIdAndTermIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        request.getTeacherId(),
                        request.getTermId(),
                        request.getDayOfWeek(),
                        request.getEndTime(),
                        request.getStartTime())) {
            throw new BusinessException(
                    "Teacher already has a class scheduled "
                            + "at this time",
                    HttpStatus.CONFLICT);
        }

        Timetable timetable = Timetable.builder()
                .school(school)
                .session(session)
                .term(term)
                .classroom(classroom)
                .subject(subject)
                .teacher(teacher)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        Timetable saved = timetableRepository.save(timetable);
        log.info("Timetable entry created: {} {} - {} {}",
                classroom.getName(), classroom.getSection(),
                request.getDayOfWeek(), request.getStartTime());

        return mapToTimetableResponse(saved);
    }

    @Override
    @Transactional
    public List<TimetableResponse> getTimetableByTerm(Long termId) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return timetableRepository
                .findAllByTermIdAndSchoolId(termId, schoolId)
                .stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<TimetableResponse> getTimetableByClassroom(
            Long classroomId, Long termId) {

        return timetableRepository
                .findAllByClassroomIdAndTermId(classroomId, termId)
                .stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<TimetableResponse> getTimetableByTeacher(
            Long teacherId, Long termId) {

        return timetableRepository
                .findAllByTeacherIdAndTermId(teacherId, termId)
                .stream()
                .map(this::mapToTimetableResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTimetableEntry(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Timetable timetable = timetableRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Timetable entry", "id", id));

        timetableRepository.delete(timetable);
        log.info("Timetable entry deleted: {}", id);
    }

    private TimetableResponse mapToTimetableResponse(
            Timetable timetable) {

        return TimetableResponse.builder()
                .id(timetable.getId())
                .sessionId(timetable.getSession().getId())
                .sessionName(timetable.getSession().getName())
                .termId(timetable.getTerm().getId())
                .termName(timetable.getTerm().getName())
                .classroomId(timetable.getClassroom().getId())
                .classroomName(timetable.getClassroom().getName())
                .classroomSection(timetable.getClassroom().getSection())
                .subjectId(timetable.getSubject().getId())
                .subjectName(timetable.getSubject().getName())
                .teacherId(timetable.getTeacher().getId())
                .teacherName(
                        timetable.getTeacher().getUser().getFirstName()
                                + " "
                                + timetable.getTeacher().getUser().getLastName())
                .dayOfWeek(timetable.getDayOfWeek())
                .startTime(timetable.getStartTime())
                .endTime(timetable.getEndTime())
                .createdAt(timetable.getCreatedAt())
                .build();
    }

    private ClassroomResponse mapToClassroomResponse(
            Classroom classroom) {

        String teacherName = null;
        Long teacherId = null;

        if (classroom.getClassTeacher() != null) {
            teacherId = classroom.getClassTeacher().getId();
            teacherName = classroom.getClassTeacher()
                    .getUser().getFirstName()
                    + " "
                    + classroom.getClassTeacher()
                    .getUser().getLastName();
        }

        return ClassroomResponse.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .section(classroom.getSection())
                .description(classroom.getDescription())
                .capacity(classroom.getCapacity())
                .classTeacherId(teacherId)
                .classTeacherName(teacherName)
                .level(classroom.getLevel())
                .active(classroom.isActive())
                .schoolName(classroom.getSchool().getName())
                .createdAt(classroom.getCreatedAt())
                .build();
    }

    private SubjectResponse mapToSubjectResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .description(subject.getDescription())
                .category(subject.getCategory())
                .isElective(subject.isElective())
                .active(subject.isActive())
                .schoolName(subject.getSchool().getName())
                .createdAt(subject.getCreatedAt())
                .build();
    }

    private TermResponse mapToTermResponse(Term term) {
        return TermResponse.builder()
                .id(term.getId())
                .sessionId(term.getSession().getId())
                .sessionName(term.getSession().getName())
                .name(term.getName())
                .startDate(term.getStartDate())
                .endDate(term.getEndDate())
                .status(term.getStatus())
                .isCurrent(term.isCurrent())
                .needsDateUpdate(term.isNeedsDateUpdate())
                .createdAt(term.getCreatedAt())
                .build();
    }
}