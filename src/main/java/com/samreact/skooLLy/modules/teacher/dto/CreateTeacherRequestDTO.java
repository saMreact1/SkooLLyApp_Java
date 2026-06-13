package com.samreact.skooLLy.modules.teacher.dto;

import com.samreact.skooLLy.modules.teacher.entity.enums.EmploymentType;
import com.samreact.skooLLy.modules.teacher.entity.enums.QualificationLevel;
import com.samreact.skooLLy.modules.user.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTeacherRequestDTO {
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

    // ── Teacher Profile Fields ────────────────────────────────

    @NotNull(message = "Join date is required")
    private LocalDate joinDate;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    private QualificationLevel highestQualification;
    private String specialization;
    private Integer yearsOfExperience;
    private String designation;

}
