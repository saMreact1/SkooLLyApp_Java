package com.samreact.skooLLy.modules.communication.dto;

import com.samreact.skooLLy.modules.communication.entity.enums.ConversationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ConversationResponse {
    private Long id;
    private String title;
    private ConversationType type;
    private Long createdById;
    private String createdByName;
    private List<ConversationParticipantResponse> participants;
    private MessageResponse lastMessage;
    private int unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}