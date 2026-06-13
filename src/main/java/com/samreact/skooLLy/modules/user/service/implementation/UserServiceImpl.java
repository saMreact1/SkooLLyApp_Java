package com.samreact.skooLLy.modules.user.service.implementation;

import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.config.JwtService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.DuplicateResourceException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import com.samreact.skooLLy.modules.student.entity.Student;
import com.samreact.skooLLy.modules.student.entity.enums.StudentStatus;
import com.samreact.skooLLy.modules.student.repository.StudentRepository;
import com.samreact.skooLLy.modules.teacher.entity.Teacher;
import com.samreact.skooLLy.modules.teacher.entity.enums.EmploymentType;
import com.samreact.skooLLy.modules.teacher.repository.TeacherRepository;
import com.samreact.skooLLy.modules.user.dto.*;
import com.samreact.skooLLy.modules.user.entity.enums.Role;
import com.samreact.skooLLy.modules.user.entity.User;
import com.samreact.skooLLy.modules.user.repository.UserRepository;
import com.samreact.skooLLy.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TeacherRepository teacherRepository;

    // Step 1
    @Override
    @Transactional
    public CheckEmailAndSchoolResponseDTO checkEmailAndSchool(
            CheckEmailAndSchoolRequestDTO request) {

        boolean emailExists = userRepository
                .findByEmailAndDeleted(request.getEmail(), false)
                .isPresent();
        boolean schoolExists = schoolRepository
                .existsByName(request.getSchoolName());

        if (emailExists) {
            String schoolName = userRepository
                    .findByEmailAndDeleted(request.getEmail(), false)
                    .map(u -> u.getSchool().getName())
                    .orElse("a school");

            return CheckEmailAndSchoolResponseDTO.builder()
                    .nextStep(0)
                    .message("You already have an account with "
                            + schoolName + ". Please login.")
                    .schoolExists(schoolExists)
                    .build();
        }

        if (schoolExists) {
            School school = schoolRepository
                    .findByName(request.getSchoolName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "School", "name", request.getSchoolName()));

            return CheckEmailAndSchoolResponseDTO.builder()
                    .nextStep(3)
                    .message("School found. Please complete "
                            + "your personal details.")
                    .schoolExists(true)
                    .schoolId(school.getId())
                    .build();
        }

        return CheckEmailAndSchoolResponseDTO.builder()
                .nextStep(2)
                .message("Please provide your school details.")
                .schoolExists(false)
                .build();
    }

    // Step 3
    @Override
    @Transactional
    public AuthResponseDTO completeRegistration(
            CompleteRegistrationRequestDTO request) {

        // 1. Check email is still available
        if (userRepository.findByEmailAndDeleted(request.getEmail(), false).isPresent()) {
            throw new DuplicateResourceException(
                    "User", "email", request.getEmail());
        }

        // 2. Check phone number is available
        if (userRepository.findByPhoneNumberAndDeleted(request.getPhoneNumber(), false).isPresent()) {
            throw new DuplicateResourceException(
                    "User", "phoneNumber", request.getPhoneNumber());
        }

        // 3. Load the school
        School school = schoolRepository
                .findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School", "id", request.getSchoolId()));

        // 4. Validate role-specific fields before creating anything
        validateRoleSpecificFields(request);

        // 5. Create the User account
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
                .role(request.getRole())
                .school(school)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {} with role: {}",
                savedUser.getEmail(), savedUser.getRole());

        // 6. Create role-specific profile
        createRoleSpecificProfile(savedUser, school, request);

        // 7. Generate JWT token
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getRole().name(), savedUser.getSchool().getId(), savedUser);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(savedUser))
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmailAndDeleted(request.getEmail(), false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "email", request.getEmail()));

        String token = jwtService.generateToken(user.getId(), user.getRole().name(), user.getSchool().getId(), user);
        log.info("User logged in: {} from school: {}",
                user.getEmail(), user.getSchool().getName());

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", id));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSearchResult> searchUsers(String query) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long currentUserId = currentUserService.getCurrentUserId();
        List<User> users = userRepository.searchByQuery(schoolId, currentUserId, query);
        return users.stream()
                .map(this::mapToSearchResult)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersInSchool(Long schoolId) {
        List<User> users = userRepository.findBySchoolIdAndDeleted(schoolId, false);
        return users.stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSchoolIdByEmail(String email) {
        Long schoolId = userRepository.getSchoolIdByEmail(email);
        log.debug("Found schoolId: {} for email: {}", schoolId, email);
        return schoolId;
    }

    // ── Private Helpers ───────────────────────────────────────

    /**
     * Validates that role-specific required fields are present
     * before any database writes happen.
     */
    private void validateRoleSpecificFields(
            CompleteRegistrationRequestDTO request) {

        if (request.getRole() == Role.STUDENT) {
            if (request.getCurrentClass() == null
                    || request.getCurrentClass().isBlank()) {
                throw new BusinessException(
                        "Current class is required for students",
                        HttpStatus.BAD_REQUEST);
            }
            if (request.getCurrentSection() == null
                    || request.getCurrentSection().isBlank()) {
                throw new BusinessException(
                        "Current section is required for students",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Creates the appropriate profile based on the user's role.
     * Currently handles STUDENT.
     * TEACHER profile will be added in the teacher module.
     */
    private void createRoleSpecificProfile(
            User user,
            School school,
            CompleteRegistrationRequestDTO request) {

        switch (request.getRole()) {

            case STUDENT -> {
                String admissionNumber =
                        generateAdmissionNumber(school);

                Student student = Student.builder()
                        .user(user)
                        .school(school)
                        .admissionNumber(admissionNumber)
                        .admissionDate(
                                request.getAdmissionDate() != null
                                        ? request.getAdmissionDate()
                                        : LocalDate.now())
                        .currentClass(request.getCurrentClass())
                        .currentSection(request.getCurrentSection())
                        .emergencyContactName(
                                request.getEmergencyContactName())
                        .emergencyContactPhone(
                                request.getEmergencyContactPhone())
                        .emergencyContactRelationship(
                                request.getEmergencyContactRelationship())
                        .bloodGroup(request.getBloodGroup())
                        .medicalConditions(request.getMedicalConditions())
                        .status(StudentStatus.ACTIVE)
                        .build();

                studentRepository.save(student);
                log.info("Student profile created: {} for user: {}",
                        admissionNumber, user.getEmail());
            }

            case TEACHER -> {
                String staffId = generateStaffId(school);

                Teacher teacher = Teacher.builder()
                        .user(user)
                        .school(school)
                        .staffId(staffId)
                        .joinDate(LocalDate.now())
                        .employmentType(request.getTeacherEmploymentType() != null
                                ? request.getTeacherEmploymentType()
                                : EmploymentType.FULL_TIME)
                        .build();

                teacherRepository.save(teacher);
                log.info("Teacher profile created: {} for user: {}",
                        staffId, user.getEmail());
            }

            default -> {
                log.info("No additional profile needed for role: {}", request.getRole());
            }
        }
    }

    /**
     * Generates a unique admission number per school.
     * Format: PREFIX-YEAR-XXXX
     * Example: GRE-2026-A1B2
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

    private UserResponseDTO mapToUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserSearchResult mapToSearchResult(User user) {
        return UserSearchResult.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .build();
    }
}