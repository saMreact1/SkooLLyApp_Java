package com.samreact.skooLLy.modules.school.service.implementation;

import com.samreact.skooLLy.exception.DuplicateResourceException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.school.dto.CreateSchoolRequestDTO;
import com.samreact.skooLLy.modules.school.dto.SchoolResponseDTO;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;

    @Override
    public SchoolResponseDTO createSchool(CreateSchoolRequestDTO request) {
        if (schoolRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "School", "name", request.getName()
            );
        }

        if (schoolRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "School", "email", request.getEmail()
            );
        }

        String schoolCode = generateSchoolCode(request.getName());

        School school = School.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .type(request.getType())
                .logoUrl(request.getLogoUrl())
                .schoolCode(schoolCode)
                .build();

        School savedSchool = schoolRepository.save(school);
        log.info("New school created: {} with code: {}",
                savedSchool.getName(), savedSchool.getSchoolCode());

        return mapToSchoolResponse(savedSchool);
    }

    @Override
    public SchoolResponseDTO getSchoolById(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("School", "id", id)
                );
        return mapToSchoolResponse(school);
    }

    @Override
    public SchoolResponseDTO updateSchool(Long id, CreateSchoolRequestDTO request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("School", "id", id)
                );

        // Update fields
        school.setName(request.getName());
        school.setEmail(request.getEmail());
        school.setPhoneNumber(request.getPhoneNumber());
        school.setAddress(request.getAddress());
        school.setCity(request.getCity());
        school.setState(request.getState());
        school.setType(request.getType());
        school.setLogoUrl(request.getLogoUrl());

        School updatedSchool = schoolRepository.save(school);
        log.info("School updated: {} with ID: {}",
                updatedSchool.getName(), updatedSchool.getId());

        return mapToSchoolResponse(updatedSchool);
    }

    @Override
    public boolean deleteSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("School", "id", id)
                );

        school.setDeleted(true);
        schoolRepository.save(school);
        log.info("School deleted: {} with ID: {}",
                school.getName(), school.getId());

        return true;
    }

    @Override
    public boolean existsByName(String name) {
        return schoolRepository.existsByName(name);
    }

    @Override
    public Long getSchoolIdByName(String name) {
        School school = schoolRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("School", "name", name)
                );
        return school.getId();
    }

    // ── Private Helpers ───────────────────────────────────────

    /**
     * Generates a unique school code.
     * Takes first 3 letters of school name + 6 random characters.
     * Example: "Greenfield Academy" → "GRE-A1B2C3"
     */
    private String generateSchoolCode(String schoolName) {
        String prefix = schoolName
                .replaceAll("[^a-zA-Z]", "")
                .toUpperCase()
                .substring(0, Math.min(3, schoolName.replaceAll(
                        "[^a-zA-Z]", "").length()));

        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return prefix + "-" + unique;
    }

    private SchoolResponseDTO mapToSchoolResponse(School school) {
        return SchoolResponseDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .email(school.getEmail())
                .phoneNumber(school.getPhoneNumber())
                .address(school.getAddress())
                .city(school.getCity())
                .state(school.getState())
//                .country(school.getCountry())
                .type(school.getType())
                .status(school.getStatus())
                .logoUrl(school.getLogoUrl())
                .schoolCode(school.getSchoolCode())
                .createdAt(school.getCreatedAt())
                .build();

    }
}
