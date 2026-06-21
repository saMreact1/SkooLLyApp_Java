package com.samreact.skooLLy.modules.user.dto;

import com.samreact.skooLLy.modules.user.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String profilePictureUrl;
}
