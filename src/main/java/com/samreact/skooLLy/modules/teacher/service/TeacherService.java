package com.samreact.skooLLy.modules.teacher.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.teacher.dto.CreateTeacherRequestDTO;
import com.samreact.skooLLy.modules.teacher.dto.TeacherResponseDTO;
import com.samreact.skooLLy.modules.teacher.dto.UpdateTeacherRequestDTO;
import com.samreact.skooLLy.modules.teacher.entity.enums.TeacherStatus;

import java.util.List;

public interface TeacherService {
    TeacherResponseDTO createTeacher(CreateTeacherRequestDTO request);

    TeacherResponseDTO getTeacherById(Long id);

    PagedResponse<TeacherResponseDTO> getAllTeachers(int page, int size);

    TeacherResponseDTO updateTeacher(Long id, UpdateTeacherRequestDTO request);

    TeacherResponseDTO updateTeacherStatus(Long id, TeacherStatus status);

    void deleteTeacher(Long id);

    long getTeacherCount();
}
