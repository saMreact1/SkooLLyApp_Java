package com.samreact.skooLLy.modules.student.service;

import com.samreact.skooLLy.modules.student.dto.CreateStudentRequestDTO;
import com.samreact.skooLLy.modules.student.dto.StudentResponseDTO;
import com.samreact.skooLLy.modules.student.dto.UpdateStudentRequestDTO;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;

import java.util.List;

public interface StudentService {
    StudentResponseDTO createStudent(CreateStudentRequestDTO request);

    StudentResponseDTO getStudentById(Long id);

    List<StudentResponseDTO> getAllStudents();

    List<StudentResponseDTO> getStudentsByClass(String className);

    StudentResponseDTO updateStudent(Long id, UpdateStudentRequestDTO request);

    StudentResponseDTO updateStudentStatus(Long id, StudentStatus status);

    void deleteStudent(Long id);
    long getStudentCount();
}
