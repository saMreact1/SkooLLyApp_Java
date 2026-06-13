package com.samreact.skooLLy.modules.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDTO {
    private String token;
    private String tokenType;
    private UserResponseDTO user;
}
