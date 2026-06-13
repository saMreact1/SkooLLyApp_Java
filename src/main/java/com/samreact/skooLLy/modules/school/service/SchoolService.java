package com.samreact.skooLLy.modules.school.service;

import com.samreact.skooLLy.modules.school.dto.CreateSchoolRequestDTO;
import com.samreact.skooLLy.modules.school.dto.SchoolResponseDTO;

public interface SchoolService {

    SchoolResponseDTO createSchool(CreateSchoolRequestDTO request);

    SchoolResponseDTO getSchoolById(Long id);

    SchoolResponseDTO updateSchool(Long id, CreateSchoolRequestDTO request);

    boolean deleteSchool(Long id);

    boolean existsByName(String name);

    Long getSchoolIdByName(String name);
}