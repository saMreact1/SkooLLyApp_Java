package com.samreact.skooLLy.modules.user.dto;

import com.samreact.skooLLy.modules.teacher.entity.enums.EmploymentType;
import com.samreact.skooLLy.modules.user.entity.enums.Gender;
import com.samreact.skooLLy.modules.user.entity.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompleteRegistrationRequestDTO {
    @NotNull(message = "School ID is required")
    private Long schoolId;

    // User account fields (required for all roles)
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Address is required")
    private String address;

    private String profilePictureUrl;

    @NotNull(message = "Role is required")
    private Role role;

    // Teacher specific fields
    private EmploymentType teacherEmploymentType;

    // Student specific fields
    private LocalDate admissionDate;
    private String currentClass;
    private String currentSection;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String bloodGroup;
    private String medicalConditions;
}