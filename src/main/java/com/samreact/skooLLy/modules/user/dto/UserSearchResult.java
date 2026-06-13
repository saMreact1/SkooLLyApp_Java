package com.samreact.skooLLy.modules.user.dto;

import com.samreact.skooLLy.modules.user.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResult {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePictureUrl;
    private Role role;
}
