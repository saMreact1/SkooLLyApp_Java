package com.samreact.skooLLy.modules.communication.dto;

import com.samreact.skooLLy.modules.communication.entity.enums.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private MessageType type;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private boolean edited;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}