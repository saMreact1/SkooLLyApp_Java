package com.samreact.skooLLy.modules.user.dto;

import com.samreact.skooLLy.modules.user.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String schoolName;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
