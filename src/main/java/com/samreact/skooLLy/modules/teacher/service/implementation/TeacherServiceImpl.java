package com.samreact.skooLLy.modules.teacher.service.implementation;

import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.DuplicateResourceException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.modules.teacher.dto.*;
import com.samreact.skooLLy.modules.teacher.entity.Teacher;
import com.samreact.skooLLy.modules.teacher.entity.enums.TeacherStatus;
import com.samreact.skooLLy.modules.teacher.repository.TeacherRepository;
import com.samreact.skooLLy.modules.teacher.service.TeacherService;
import com.samreact.skooLLy.modules.user.entity.enums.Role;
import com.samreact.skooLLy.modules.user.entity.User;
import com.samreact.skooLLy.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TeacherResponseDTO createTeacher(CreateTeacherRequestDTO request) {
        // 1. Get current school from logged in admin
        Long schoolId = currentUserService.getCurrentSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", schoolId));

        // 2. Check email is not already taken platform-wide
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User", "email", request.getEmail());
        }

        // 3. Check phone number is not already taken
        if (userRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {
            throw new DuplicateResourceException(
                    "User", "phoneNumber", request.getPhoneNumber());
        }

        // 4. Create the User account with role TEACHER
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(
                        request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .profilePictureUrl(request.getProfilePictureUrl())
                .role(Role.TEACHER)
                .school(school)
                .build();

        User savedUser = userRepository.save(user);

        // 5. Generate unique staff ID
        String staffId = generateStaffId(school);

        // 6. Create the Teacher profile
        Teacher teacher = Teacher.builder()
                .user(savedUser)
                .school(school)
                .staffId(staffId)
                .joinDate(request.getJoinDate() != null
                        ? request.getJoinDate()
                        : LocalDate.now())
                .highestQualification(
                        request.getHighestQualification())
                .specialization(request.getSpecialization())
                .yearsOfExperience(request.getYearsOfExperience())
                .employmentType(request.getEmploymentType())
                .designation(request.getDesignation())
                .build();

        Teacher saved = teacherRepository.save(teacher);

        log.info("Teacher created: {} - {} at school: {}",
                saved.getStaffId(),
                savedUser.getEmail(),
                school.getName());

        return mapToTeacherResponseDTO(saved);
    }

    @Override
    @Transactional
    public TeacherResponseDTO getTeacherById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Teacher teacher = teacherRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher", "id", id));

        return mapToTeacherResponseDTO(teacher);
    }

    @Override
    @Transactional
    public PagedResponse<TeacherResponseDTO> getAllTeachers(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<Teacher> teacherPage = teacherRepository
                .findAllBySchoolIdAndDeleted(schoolId, false, PageRequest.of(page, size));

        return PageUtil.from(teacherPage, this::mapToTeacherResponseDTO);
    }

    @Override
    @Transactional
    public TeacherResponseDTO updateTeacher(Long id, UpdateTeacherRequestDTO request) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        Teacher teacher = teacherRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher", "id", id));

        if (request.getHighestQualification() != null) {
            teacher.setHighestQualification(
                    request.getHighestQualification());
        }
        if (request.getSpecialization() != null) {
            teacher.setSpecialization(request.getSpecialization());
        }
        if (request.getYearsOfExperience() != null) {
            teacher.setYearsOfExperience(
                    request.getYearsOfExperience());
        }
        if (request.getEmploymentType() != null) {
            teacher.setEmploymentType(request.getEmploymentType());
        }
        if (request.getDesignation() != null) {
            teacher.setDesignation(request.getDesignation());
        }

        Teacher updated = teacherRepository.save(teacher);
        log.info("Teacher updated: {}", updated.getStaffId());

        return mapToTeacherResponseDTO(updated);
    }

    @Override
    @Transactional
    public TeacherResponseDTO updateTeacherStatus(Long id, TeacherStatus status) {

        Long schoolId = currentUserService.getCurrentSchoolId();

        Teacher teacher = teacherRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher", "id", id));

        teacher.setStatus(status);
        Teacher updated = teacherRepository.save(teacher);

        log.info("Teacher {} status updated to: {}", updated.getStaffId(), status);

        return mapToTeacherResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Teacher teacher = teacherRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher", "id", id));

        teacher.setDeleted(true);
        teacherRepository.save(teacher);

        log.info("Teacher soft deleted: {}", teacher.getStaffId());
    }

    @Override
    @Transactional
    public long getTeacherCount() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        return teacherRepository.countBySchoolIdAndDeleted(schoolId, false);
    }

        /**
     * Generates a unique staff ID per school.
     * Format: PREFIX-TCH-XXXX
     * Example: GRE-TCH-A1B2
     */
    private String generateStaffId(School school) {
        String prefix = school.getSchoolCode()
                .substring(0, Math.min(3,
                        school.getSchoolCode().length()));
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 4)
                .toUpperCase();

        return prefix + "-TCH-" + unique;
    }

    private TeacherResponseDTO mapToTeacherResponseDTO(Teacher teacher) {
        return TeacherResponseDTO.builder()
                .id(teacher.getId())
                .userId(teacher.getUser().getId())
                .firstName(teacher.getUser().getFirstName())
                .lastName(teacher.getUser().getLastName())
                .email(teacher.getUser().getEmail())
                .phoneNumber(teacher.getUser().getPhoneNumber())
                .staffId(teacher.getStaffId())
                .joinDate(teacher.getJoinDate())
                .highestQualification(
                        teacher.getHighestQualification())
                .specialization(teacher.getSpecialization())
                .yearsOfExperience(teacher.getYearsOfExperience())
                .employmentType(teacher.getEmploymentType())
                .designation(teacher.getDesignation())
                .status(teacher.getStatus())
                .schoolName(teacher.getSchool().getName())
                .createdAt(teacher.getCreatedAt())
                .build();
    }
}