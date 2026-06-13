package com.samreact.skooLLy.modules.school.dto;

import com.samreact.skooLLy.modules.school.entity.enums.SchoolType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSchoolRequestDTO {

    @NotBlank(message = "School name is required")
    private String name;

    @NotBlank(message = "School email is required")
    @Email(message = "School email must be valid")
    private String email;

    @NotBlank(message = "School phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotNull(message = "School type is required")
    private SchoolType type;

    private String logoUrl;
}