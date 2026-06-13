package com.samreact.skooLLy.modules.communication.service;

import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.enums.ConversationType;

import java.util.List;

public interface MessagingService {
    ConversationResponse createConversation(CreateConversationRequest request);

    ConversationResponse getConversationById(Long id);

    List<ConversationResponse> getUserConversations();

    List<ConversationResponse> getUserConversationsByType(ConversationType type);

    ConversationResponse updateConversation(Long id, UpdateConversationRequest request);

    void deleteConversation(Long id);

    void leaveConversation(Long conversationId);

    void addParticipants(Long conversationId, List<Long> userIds);

    void removeParticipant(Long conversationId, Long userId);

    MessageResponse sendMessage(CreateMessageRequest request);

    MessageResponse getMessageById(Long conversationId, Long messageId);

    List<MessageResponse> getConversationMessages(Long conversationId, int page, int size);

    MessageResponse updateMessage(Long conversationId, Long messageId, UpdateMessageRequest request);

    void deleteMessage(Long conversationId, Long messageId);

    void markConversationAsRead(Long conversationId);

    long getUnreadCount(Long conversationId);

    long getTotalUnreadCount();
}