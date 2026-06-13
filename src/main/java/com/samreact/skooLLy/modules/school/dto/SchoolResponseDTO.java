package com.samreact.skooLLy.modules.school.dto;

import com.samreact.skooLLy.modules.school.entity.enums.SchoolStatus;
import com.samreact.skooLLy.modules.school.entity.enums.SchoolType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SchoolResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private SchoolType type;
    private SchoolStatus status;
    private String logoUrl;
    private String schoolCode;
    private LocalDateTime createdAt;
}
