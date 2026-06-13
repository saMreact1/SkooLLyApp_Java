package com.samreact.skooLLy.modules.communication.dto;

import com.samreact.skooLLy.modules.communication.entity.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageRequest {
    @NotNull
    private Long conversationId;

    @NotBlank
    private String content;

    private MessageType type = MessageType.TEXT;

    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
}