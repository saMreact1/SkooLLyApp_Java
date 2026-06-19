package com.samreact.skooLLy.modules.user.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.modules.academic.repository.ClassroomRepository;
import com.samreact.skooLLy.modules.school.dto.CreateSchoolRequestDTO;
import com.samreact.skooLLy.modules.school.dto.SchoolResponseDTO;
import com.samreact.skooLLy.modules.school.service.SchoolService;
import com.samreact.skooLLy.modules.user.dto.*;
import com.samreact.skooLLy.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final SchoolService schoolService;
    private final ClassroomRepository classroomRepository;

    /**
     * STEP 1 — Check email and school name
     *
     * POST /api/auth/check
     *
     * Request:
     * {
     *   "email": "sam@gmail.com",
     *   "schoolName": "Greenfield Academy"
     * }
     * Response:
     * {
     *   "success": true,
     *   "message": "Please provide your school details.",
     *   "data": {
     *     "nextStep": 2,
     *     "schoolExists": false,
     *     "schoolId": null
     *   }
     * }
     **/
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<CheckEmailAndSchoolResponseDTO>> checkEmailAndSchool(@Valid @RequestBody CheckEmailAndSchoolRequestDTO request) {
        CheckEmailAndSchoolResponseDTO result = userService.checkEmailAndSchool(request);
        return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
    }

    /**
     * STEP 2 — Create the school
     *
     * POST /api/auth/school/register
     *
     * Request:
     * {
     *   "name": "Greenfield Academy",
     *   "email": "info@greenfield.com",
     *   "phoneNumber": "08012345678",
     *   "address": "123 School Road",
     *   "city": "Lagos",
     *   "state": "Lagos",
     *   "country": "Nigeria",
     *   "type": "SECONDARY",
     *   "logoUrl": null
     * }
     */
    @PostMapping("/school/register")
    public ResponseEntity<ApiResponse<SchoolResponseDTO>> registerSchool(@Valid @RequestBody CreateSchoolRequestDTO request) {
        SchoolResponseDTO school =schoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("School registered successfully", school));
    }

    /**
     * STEP 3 — Complete personal registration
     *
     * POST /api/auth/register
     *
     * Request:
     * {
     *   "schoolId": 1,
     *   "firstName": "Samuel",
     *   "lastName": "Adeyemi",
     *   "email": "sam@gmail.com",
     *   "password": "password123",
     *   "phoneNumber": "08012345678",
     *   "dateOfBirth": "1990-01-15",
     *   "gender": "MALE",
     *   "address": "45 Home Street, Lagos",
     *   "profilePictureUrl": null,
     *   "role": "SUPER_ADMIN"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody CompleteRegistrationRequestDTO request) {
        AuthResponseDTO response = userService.completeRegistration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User registered successfully", response));
    }

    /**
     * LOGIN
     *
     * POST /api/auth/login
     *
     * Request:
     * {
     *   "email": "sam@gmail.com",
     *   "password": "password123"
     * }
     *
     * Response includes JWT token to use in future requests
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Login successful", response));
    }

    /**
     * GET school classrooms (public — used during registration)
     *
     * GET /api/auth/school/{schoolId}/classrooms
     */
    @GetMapping("/school/{schoolId}/classrooms")
    public ResponseEntity<ApiResponse<List<String>>> getSchoolClassrooms(
            @PathVariable Long schoolId) {

        List<String> classrooms = classroomRepository
                .findAllBySchoolId(schoolId)
                .stream()
                .map(c -> c.getName())
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Classrooms retrieved", classrooms));
    }
}
