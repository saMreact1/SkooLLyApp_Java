package com.samreact.skooLLy.modules.student.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.student.dto.*;
import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;
import com.samreact.skooLLy.modules.student.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * CREATE a new student
     * Only ADMIN and SUPER_ADMIN can create students
     *
     * POST /api/students
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody CreateStudentRequestDTO request) {

        StudentResponseDTO student = studentService.createStudent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Student created successfully", student));
    }

    /**
     * GET all students in the current school
     * ADMIN, SUPER_ADMIN and TEACHER can view all students
     *
     * GET /api/students
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponseDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<StudentResponseDTO> students = studentService.getAllStudents(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Students retrieved successfully", students));
    }

    /**
     * GET a single student by ID
     *
     * GET /api/students/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(
            @PathVariable Long id) {

        StudentResponseDTO student = studentService.getStudentById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student retrieved successfully", student));
    }

    /**
     * GET the current student's own profile
     *
     * GET /api/students/me
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getMyStudentProfile() {
        StudentResponseDTO student = studentService.getMyStudentProfile();
        return ResponseEntity.ok(
                ApiResponse.success("Student profile retrieved", student));
    }

    /**
     * GET a student by their linked user ID
     *
     * GET /api/students/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByUserId(
            @PathVariable Long userId) {

        StudentResponseDTO student = studentService.getStudentByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student retrieved successfully", student));
    }

    /**
     * GET all students in a specific class
     *
     * GET /api/students/class/{className}
     */
    @GetMapping("/class/{className}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponseDTO>>> getStudentsByClass(
            @PathVariable String className,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<StudentResponseDTO> students = studentService.getStudentsByClass(className, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Students retrieved successfully", students));
    }

    /**
     * UPDATE a student profile
     *
     * PUT /api/students/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequestDTO request) {

        StudentResponseDTO student =
                studentService.updateStudent(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student updated successfully", student));
    }

    /**
     * UPDATE student status
     * e.g. ACTIVE, SUSPENDED, GRADUATED
     *
     * PATCH /api/students/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudentStatus(
            @PathVariable Long id,
            @RequestParam StudentStatus status) {

        StudentResponseDTO student =
                studentService.updateStudentStatus(id, status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student status updated successfully", student));
    }

    /**
     * DELETE a student (soft delete)
     *
     * DELETE /api/students/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponse.success("Student deleted successfully"));
    }

    /**
     * GET total student count for current school
     *
     * GET /api/students/count
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getStudentCount() {

        long count = studentService.getStudentCount();

        return ResponseEntity.ok(
                ApiResponse.success("Student count retrieved", count));
    }
}