package com.samreact.skooLLy.modules.student.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.student.dto.CreateStudentRequestDTO;
import com.samreact.skooLLy.modules.student.dto.StudentResponseDTO;
import com.samreact.skooLLy.modules.student.dto.UpdateStudentRequestDTO;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;

import java.util.List;

public interface StudentService {
    StudentResponseDTO createStudent(CreateStudentRequestDTO request);

    StudentResponseDTO getStudentById(Long id);

    PagedResponse<StudentResponseDTO> getAllStudents(int page, int size);

    PagedResponse<StudentResponseDTO> getStudentsByClass(String className, int page, int size);

    StudentResponseDTO updateStudent(Long id, UpdateStudentRequestDTO request);

    StudentResponseDTO updateStudentStatus(Long id, StudentStatus status);

    StudentResponseDTO getStudentByUserId(Long userId);

    StudentResponseDTO getMyStudentProfile();

    void deleteStudent(Long id);
    long getStudentCount();
}
