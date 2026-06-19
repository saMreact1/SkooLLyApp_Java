package com.samreact.skooLLy.modules.teacher.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.teacher.dto.*;
import com.samreact.skooLLy.modules.teacher.entity.enums.TeacherStatus;
import com.samreact.skooLLy.modules.teacher.service.TeacherService;
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
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * CREATE a new teacher
     * Only ADMIN and SUPER_ADMIN can create teachers
     *
     * POST /api/teachers
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> createTeacher(
            @Valid @RequestBody CreateTeacherRequestDTO request) {

        TeacherResponseDTO teacher =
                teacherService.createTeacher(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Teacher created successfully", teacher));
    }

    /**
     * GET the currently logged-in teacher's profile
     *
     * GET /api/teachers/me
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getMyTeacherProfile() {
        TeacherResponseDTO teacher = teacherService.getMyTeacherProfile();
        return ResponseEntity.ok(ApiResponse.success("Teacher profile retrieved", teacher));
    }

    /**
     * GET all teachers in the current school
     *
     * GET /api/teachers
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<PagedResponse<TeacherResponseDTO>>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<TeacherResponseDTO> teachers = teacherService.getAllTeachers(page, size);

        return ResponseEntity.ok(ApiResponse.success("Teachers retrieved successfully", teachers));
    }

    /**
     * GET a single teacher by ID
     *
     * GET /api/teachers/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherById(
            @PathVariable Long id) {

        TeacherResponseDTO teacher = teacherService.getTeacherById(id);

        return ResponseEntity.ok(ApiResponse.success("Teacher retrieved successfully", teacher));
    }

    /**
     * UPDATE a teacher profile
     *
     * PUT /api/teachers/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeacherRequestDTO request) {

        TeacherResponseDTO teacher =
                teacherService.updateTeacher(id, request);

        return ResponseEntity.ok(ApiResponse.success("Teacher updated successfully", teacher));
    }

    /**
     * UPDATE teacher status
     *
     * PATCH /api/teachers/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> updateTeacherStatus(
            @PathVariable Long id,
            @RequestParam TeacherStatus status) {

        TeacherResponseDTO teacher =
                teacherService.updateTeacherStatus(id, status);

        return ResponseEntity.ok(ApiResponse.success("Teacher status updated successfully", teacher));
    }

    /**
     * DELETE a teacher (soft delete)
     *
     * DELETE /api/teachers/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteTeacher(
            @PathVariable Long id) {

        teacherService.deleteTeacher(id);

        return ResponseEntity.ok(ApiResponse.success("Teacher deleted successfully"));
    }

    /**
     * GET total teacher count for current school
     *
     * GET /api/teachers/count
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTeacherCount() {

        long count = teacherService.getTeacherCount();

        return ResponseEntity.ok(ApiResponse.success("Teacher count retrieved", count));
    }
}