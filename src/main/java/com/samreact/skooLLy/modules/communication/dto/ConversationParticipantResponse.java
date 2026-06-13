package com.samreact.skooLLy.modules.communication.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConversationParticipantResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean muted;
    private LocalDateTime lastReadAt;
    private LocalDateTime joinedAt;
}