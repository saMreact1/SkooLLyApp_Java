package com.samreact.skooLLy.modules.student.service.implementation;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.common.util.PageUtil;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.DuplicateResourceException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.student.dto.CreateStudentRequestDTO;
import com.samreact.skooLLy.modules.student.dto.StudentResponseDTO;
import com.samreact.skooLLy.modules.student.dto.UpdateStudentRequestDTO;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;
import com.samreact.skooLLy.modules.student.repository.StudentRepository;
import com.samreact.skooLLy.modules.student.service.StudentService;
import com.samreact.skooLLy.modules.user.entity.User;
import com.samreact.skooLLy.modules.user.entity.enums.Role;
import com.samreact.skooLLy.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public StudentResponseDTO createStudent(CreateStudentRequestDTO request) {

        // 1. Get current school from the logged in admin
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
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException(
                    "User", "phoneNumber", request.getPhoneNumber());
        }

        // 4. Create the User account with role STUDENT
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .profilePictureUrl(request.getProfilePictureUrl())
                .role(Role.STUDENT)
                .school(school)
                .build();

        User savedUser = userRepository.save(user);

        // 5. Generate admission number
        String admissionNumber = generateAdmissionNumber(school);

        // 6. Create the Student profile linked to the User
        Student student = Student.builder()
                .user(savedUser)
                .school(school)
                .admissionNumber(admissionNumber)
                .admissionDate(request.getAdmissionDate() != null
                        ? request.getAdmissionDate()
                        : LocalDate.now())
                .currentClass(request.getCurrentClass())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelationship(
                        request.getEmergencyContactRelationship())
                .bloodGroup(request.getBloodGroup())
                .medicalConditions(request.getMedicalConditions())
                .build();

        Student saved = studentRepository.save(student);

        log.info("Student created: {} - {} at school: {}",
                saved.getAdmissionNumber(),
                savedUser.getEmail(),
                school.getName());

        return mapToStudentResponse(saved);
    }

    @Override
    @Transactional
    public StudentResponseDTO getStudentById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", id));

        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO getStudentByUserId(Long userId) {
        Student student = studentRepository
                .findByUserIdAndDeleted(userId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "userId", userId));

        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO getMyStudentProfile() {
        Long userId = currentUserService.getCurrentUserId();
        Student student = studentRepository
                .findByUserIdAndDeleted(userId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "userId", userId));

        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public PagedResponse<StudentResponseDTO> getAllStudents(int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<Student> studentPage = studentRepository
                .findAllBySchoolIdAndDeleted(schoolId, false, PageRequest.of(page, size));

        return PageUtil.from(studentPage, this::mapToStudentResponse);
    }

    @Override
    @Transactional
    public PagedResponse<StudentResponseDTO> getStudentsByClass(String className, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Page<Student> studentPage = studentRepository
                .findAllBySchoolIdAndCurrentClassAndDeleted(schoolId, className, false, PageRequest.of(page, size));

        return PageUtil.from(studentPage, this::mapToStudentResponse);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(Long id,
                                          UpdateStudentRequestDTO request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", id));

        // Only update fields that were provided
        if (request.getCurrentClass() != null) {
            student.setCurrentClass(request.getCurrentClass());
        }
        if (request.getEmergencyContactName() != null) {
            student.setEmergencyContactName(
                    request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            student.setEmergencyContactPhone(
                    request.getEmergencyContactPhone());
        }
        if (request.getEmergencyContactRelationship() != null) {
            student.setEmergencyContactRelationship(
                    request.getEmergencyContactRelationship());
        }
        if (request.getBloodGroup() != null) {
            student.setBloodGroup(request.getBloodGroup());
        }
        if (request.getMedicalConditions() != null) {
            student.setMedicalConditions(request.getMedicalConditions());
        }

        Student updated = studentRepository.save(student);
        log.info("Student updated: {}", updated.getAdmissionNumber());

        return mapToStudentResponse(updated);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudentStatus(Long id,
                                                StudentStatus status) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", id));

        student.setStatus(status);
        Student updated = studentRepository.save(student);

        log.info("Student {} status updated to: {}",
                updated.getAdmissionNumber(), status);

        return mapToStudentResponse(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Student student = studentRepository
                .findByIdAndSchoolIdAndDeleted(id, schoolId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", id));

        student.setDeleted(true);
        studentRepository.save(student);

        log.info("Student soft deleted: {}",
                student.getAdmissionNumber());
    }

    @Override
    @Transactional
    public long getStudentCount() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        return studentRepository.countBySchoolIdAndDeleted(schoolId, false);
    }

    // ── Private Helpers ───────────────────────────────────────

    /**
     * Generates a unique admission number per school.
     * Format: SCH-YEAR-XXXX
     * Example: GRE-2026-0042
     */
    private String generateAdmissionNumber(School school) {
        String prefix = school.getSchoolCode()
                .substring(0, Math.min(3,
                        school.getSchoolCode().length()));
        String year = String.valueOf(LocalDate.now().getYear());
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 4)
                .toUpperCase();

        return prefix + "-" + year + "-" + unique;
    }

    private StudentResponseDTO mapToStudentResponse(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .firstName(student.getUser().getFirstName())
                .lastName(student.getUser().getLastName())
                .email(student.getUser().getEmail())
                .admissionNumber(student.getAdmissionNumber())
                .admissionDate(student.getAdmissionDate())
                .currentClass(student.getCurrentClass())
                .status(student.getStatus())
                .bloodGroup(student.getBloodGroup())
                .emergencyContactName(student.getEmergencyContactName())
                .emergencyContactPhone(student.getEmergencyContactPhone())
                .emergencyContactRelationship(
                        student.getEmergencyContactRelationship())
                .schoolName(student.getSchool().getName())
                .profilePictureUrl(student.getUser().getProfilePictureUrl())
                .createdAt(student.getCreatedAt())
                .build();
    }
}
