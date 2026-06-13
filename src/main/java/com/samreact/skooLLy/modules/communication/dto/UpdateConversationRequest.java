package com.samreact.skooLLy.modules.communication.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateConversationRequest {
    private String title;
    private List<Long> participantIds;
}