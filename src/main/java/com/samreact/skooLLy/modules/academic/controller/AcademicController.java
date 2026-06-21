package com.samreact.skooLLy.modules.academic.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.modules.academic.dto.*;
import com.samreact.skooLLy.modules.academic.service.AcademicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
public class AcademicController {
    private final AcademicService academicService;
    private final CurrentUserService currentUserService;

    /**
     * POST /api/academic/sessions
     */
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @Valid @RequestBody CreateSessionRequest request) {

        SessionResponse session =
                academicService.createSession(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Session created successfully", session));
    }

    /**
     * GET /api/academic/sessions
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<SessionResponse>>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<SessionResponse> sessions = academicService.getAllSessions(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Sessions retrieved successfully", sessions));
    }

    /**
     * GET /api/academic/sessions/{id}
     */
    @GetMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<SessionResponse>> getSessionById(
            @PathVariable Long id) {

        SessionResponse session =
                academicService.getSessionById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Session retrieved successfully", session));
    }

    /**
     * GET /api/academic/sessions/current
     */
    @GetMapping("/sessions/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<SessionResponse>> getCurrentSession() {

        SessionResponse session =
                academicService.getCurrentSession();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current session retrieved", session));
    }

    /**
     * PATCH /api/academic/sessions/{id}/current
     */
    @PatchMapping("/sessions/{id}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SessionResponse>> setCurrentSession(
            @PathVariable Long id) {

        SessionResponse session =
                academicService.setCurrentSession(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current session updated", session));
    }

    /**
     * DELETE /api/academic/sessions/{id}
     */
    @DeleteMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteSession(
            @PathVariable Long id) {

        academicService.deleteSession(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Session deleted successfully"));
    }

    // ── Term Endpoints ────────────────────────────────────────
    /**
     * POST /api/academic/terms
     */
    @PostMapping("/terms")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TermResponse>> createTerm(
            @Valid @RequestBody CreateTermRequest request) {

        TermResponse term = academicService.createTerm(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Term created successfully", term));
    }

    /**
     * GET /api/academic/terms/{id}
     */
    @GetMapping("/terms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<TermResponse>> getTermById(
            @PathVariable Long id) {

        TermResponse term = academicService.getTermById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Term retrieved successfully", term));
    }

    /**
     * GET /api/academic/sessions/{sessionId}/terms
     */
    @GetMapping("/sessions/{sessionId}/terms")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<TermResponse>>> getTermsBySession(
            @PathVariable Long sessionId) {

        List<TermResponse> terms =
                academicService.getTermsBySession(sessionId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Terms retrieved successfully", terms));
    }

    /**
     * PUT /api/academic/terms/{id}
     */
    @PutMapping("/terms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TermResponse>> updateTerm(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTermRequest request) {

        TermResponse term = academicService.updateTerm(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Term updated successfully", term));
    }

    /**
     * GET /api/academic/terms/current
     */
    @GetMapping("/terms/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<TermResponse>> getCurrentTerm() {

        TermResponse term = academicService.getCurrentTerm();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current term retrieved", term));
    }

    /**
     * PATCH /api/academic/terms/{id}/current
     */
    @PatchMapping("/terms/{id}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TermResponse>> setCurrentTerm(
            @PathVariable Long id) {

        TermResponse term = academicService.setCurrentTerm(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current term updated", term));
    }

    /**
     * DELETE /api/academic/terms/{id}
     */
    @DeleteMapping("/terms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteTerm(
            @PathVariable Long id) {

        academicService.deleteTerm(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Term deleted successfully"));
    }

    // ── Subject Endpoints ─────────────────────────────────────
    /**
     * POST /api/academic/subjects
     */
    @PostMapping("/subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(
            @Valid @RequestBody CreateSubjectRequest request) {

        SubjectResponse subject =
                academicService.createSubject(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Subject created successfully", subject));
    }

    /**
     * GET /api/academic/subjects
     */
    @GetMapping("/subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectResponse>>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<SubjectResponse> subjects = academicService.getAllSubjects(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Subjects retrieved successfully", subjects));
    }

    /**
     * GET /api/academic/subjects/{id}
     */
    @GetMapping("/subjects/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectById(
            @PathVariable Long id) {

        SubjectResponse subject =
                academicService.getSubjectById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject retrieved successfully", subject));
    }

    /**
     * GET /api/academic/subjects/electives
     */
    @GetMapping("/subjects/electives")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getElectiveSubjects() {

        List<SubjectResponse> subjects =
                academicService.getElectiveSubjects();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Elective subjects retrieved", subjects));
    }

    /**
     * PUT /api/academic/subjects/{id}
     */
    @PutMapping("/subjects/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubjectRequest request) {

        SubjectResponse subject =
                academicService.updateSubject(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject updated successfully", subject));
    }

    /**
     * DELETE /api/academic/subjects/{id}
     */
    @DeleteMapping("/subjects/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteSubject(
            @PathVariable Long id) {

        academicService.deleteSubject(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject deleted successfully"));
    }

    // Classroom Endpoints
    @PostMapping("/classrooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> createClassroom(
            @Valid @RequestBody CreateClassroomRequest request) {

        ClassroomResponse classroom =
                academicService.createClassroom(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Classroom created successfully", classroom));
    }

    @GetMapping("/classrooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<ClassroomResponse>>> getAllClassrooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ClassroomResponse> classrooms = academicService.getAllClassrooms(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Classrooms retrieved successfully", classrooms));
    }

    @GetMapping("/classrooms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> getClassroomById(
            @PathVariable Long id) {

        ClassroomResponse classroom =
                academicService.getClassroomById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classroom retrieved successfully", classroom));
    }

    @GetMapping("/classrooms/level/{level}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> getClassroomsByLevel(
            @PathVariable String level) {

        List<ClassroomResponse> classrooms =
                academicService.getClassroomsByLevel(level);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classrooms retrieved successfully",
                        classrooms));
    }

    @PutMapping("/classrooms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> updateClassroom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClassroomRequest request) {

        ClassroomResponse classroom =
                academicService.updateClassroom(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classroom updated successfully", classroom));
    }

    @DeleteMapping("/classrooms/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteClassroom(@PathVariable Long id) {
        academicService.deleteClassroom(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classroom deleted successfully"));
    }

    @GetMapping("/classrooms/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getClassroomCount() {
        long count = academicService.getClassroomCount();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classroom count retrieved", count));
    }

    // Timetable Endpoints
    @PostMapping("/timetable")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TimetableResponse>> createTimetableEntry(
            @Valid @RequestBody CreateTimetableRequest request) {

        TimetableResponse entry =
                academicService.createTimetableEntry(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Timetable entry created successfully",
                        entry));
    }

    @GetMapping("/timetable/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetableByTerm(
            @PathVariable Long termId) {

        List<TimetableResponse> timetable =
                academicService.getTimetableByTerm(termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Timetable retrieved successfully",
                        timetable));
    }

    @GetMapping("/timetable/classroom/{classroomId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetableByClassroom(
            @PathVariable Long classroomId,
            @PathVariable Long termId) {

        List<TimetableResponse> timetable =
                academicService.getTimetableByClassroom(
                        classroomId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Classroom timetable retrieved",
                        timetable));
    }

    @GetMapping("/timetable/teacher/{teacherId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetableByTeacher(
            @PathVariable Long teacherId,
            @PathVariable Long termId) {

        List<TimetableResponse> timetable =
                academicService.getTimetableByTeacher(
                        teacherId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teacher timetable retrieved",
                        timetable));
    }

    @PutMapping("/timetable/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TimetableResponse>> updateTimetableEntry(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTimetableRequest request) {

        TimetableResponse updated =
                academicService.updateTimetableEntry(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Timetable entry updated successfully", updated));
    }

    @DeleteMapping("/timetable/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteTimetableEntry(
            @PathVariable Long id) {

        academicService.deleteTimetableEntry(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Timetable entry deleted successfully"));
    }

    // ── Enrollment Endpoints ──────────────────────────────────

    /**
     * POST /api/academic/enrollments/me
     * Student self-enrollment
     */
    @PostMapping("/enrollments/me")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponse>>> enrollMe(
            @RequestBody EnrollMeRequest request) {

        List<StudentSubjectResponse> enrollments =
                academicService.enrollMe(request.getSubjectIds(), request.getTermId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Enrolled successfully", enrollments));
    }

    /**
     * DELETE /api/academic/enrollments/me?subjectId=&termId=
     * Student self-drop
     */
    @DeleteMapping("/enrollments/me")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<ApiResponse<Object>> dropMySubject(
            @RequestParam Long subjectId,
            @RequestParam Long termId) {

        academicService.dropMySubject(subjectId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dropped from subject successfully"));
    }

    /**
     * GET /api/academic/enrollments/me/term/{termId}
     * Get current student's enrolled subjects for a term
     */
    @GetMapping("/enrollments/me/term/{termId}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponse>>> getMyEnrolledSubjects(
            @PathVariable Long termId) {

        List<StudentSubjectResponse> subjects =
                academicService.getMyStudentSubjects(termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Enrolled subjects retrieved", subjects));
    }

    /**
     * POST /api/academic/enrollments
     */
    @PostMapping("/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponse>>> enrollStudent(
            @Valid @RequestBody EnrollStudentRequest request) {

        List<StudentSubjectResponse> enrollments =
                academicService.enrollStudent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Student enrolled successfully", enrollments));
    }

    /**
     * GET /api/academic/enrollments/student/{studentId}/term/{termId}
     */
    @GetMapping("/enrollments/student/{studentId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponse>>> getStudentSubjects(
            @PathVariable Long studentId,
            @PathVariable Long termId) {

        List<StudentSubjectResponse> subjects =
                academicService.getStudentSubjects(studentId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student subjects retrieved successfully", subjects));
    }

    /**
     * GET /api/academic/enrollments/subject/{subjectId}/term/{termId}
     */
    @GetMapping("/enrollments/subject/{subjectId}/term/{termId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrolledStudentResponse>>> getSubjectStudents(
            @PathVariable Long subjectId,
            @PathVariable Long termId) {

        List<EnrolledStudentResponse> students =
                academicService.getSubjectStudents(subjectId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject students retrieved successfully", students));
    }

    /**
     * DELETE /api/academic/enrollments?studentId=&subjectId=&termId=
     */
    @DeleteMapping("/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> dropStudentFromSubject(
            @RequestParam Long studentId,
            @RequestParam Long subjectId,
            @RequestParam Long termId) {

        academicService.dropStudentFromSubject(studentId, subjectId, termId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student dropped from subject successfully"));
    }
}