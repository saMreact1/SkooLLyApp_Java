package com.samreact.skooLLy.modules.school.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.school.dto.SchoolResponseDTO;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolRepository schoolRepository;
    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SchoolResponseDTO>> getCurrentSchool() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

        SchoolResponseDTO dto = SchoolResponseDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .email(school.getEmail())
                .phoneNumber(school.getPhoneNumber())
                .address(school.getAddress())
                .city(school.getCity())
                .state(school.getState())
                .type(school.getType())
                .status(school.getStatus())
                .logoUrl(school.getLogoUrl())
                .schoolCode(school.getSchoolCode())
                .createdAt(school.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success("School retrieved", dto));
    }
}
