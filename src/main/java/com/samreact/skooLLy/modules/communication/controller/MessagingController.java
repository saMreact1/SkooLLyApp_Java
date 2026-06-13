package com.samreact.skooLLy.modules.communication.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.enums.ConversationType;
import com.samreact.skooLLy.modules.communication.service.MessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;

    @PostMapping("/conversations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        ConversationResponse response = messagingService.createConversation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conversation created successfully", response));
    }

    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getUserConversations() {
        List<ConversationResponse> conversations = messagingService.getUserConversations();
        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved successfully", conversations));
    }

    @GetMapping("/conversations/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getUserConversationsByType(
            @PathVariable ConversationType type) {
        List<ConversationResponse> conversations = messagingService.getUserConversationsByType(type);
        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved successfully", conversations));
    }

    @GetMapping("/conversations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversationById(@PathVariable Long id) {
        ConversationResponse response = messagingService.getConversationById(id);
        return ResponseEntity.ok(ApiResponse.success("Conversation retrieved successfully", response));
    }

    @PutMapping("/conversations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @PathVariable Long id, @Valid @RequestBody UpdateConversationRequest request) {
        ConversationResponse response = messagingService.updateConversation(id, request);
        return ResponseEntity.ok(ApiResponse.success("Conversation updated successfully", response));
    }

    @DeleteMapping("/conversations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(@PathVariable Long id) {
        messagingService.deleteConversation(id);
        return ResponseEntity.ok(ApiResponse.success("Conversation deleted successfully"));
    }

    @PostMapping("/conversations/{id}/leave")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Void>> leaveConversation(@PathVariable Long id) {
        messagingService.leaveConversation(id);
        return ResponseEntity.ok(ApiResponse.success("Left conversation successfully"));
    }

    @PostMapping("/conversations/{id}/participants")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> addParticipants(
            @PathVariable Long id, @RequestBody List<Long> userIds) {
        messagingService.addParticipants(id, userIds);
        return ResponseEntity.ok(ApiResponse.success("Participants added successfully"));
    }

    @DeleteMapping("/conversations/{conversationId}/participants/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeParticipant(
            @PathVariable Long conversationId, @PathVariable Long userId) {
        messagingService.removeParticipant(conversationId, userId);
        return ResponseEntity.ok(ApiResponse.success("Participant removed successfully"));
    }

    @PostMapping("/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody CreateMessageRequest request) {
        MessageResponse response = messagingService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", response));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<MessageResponse> messages = messagingService.getConversationMessages(conversationId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved successfully", messages));
    }

    @GetMapping("/conversations/{conversationId}/messages/{messageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(
            @PathVariable Long conversationId, @PathVariable Long messageId) {
        MessageResponse response = messagingService.getMessageById(conversationId, messageId);
        return ResponseEntity.ok(ApiResponse.success("Message retrieved successfully", response));
    }

    @PutMapping("/conversations/{conversationId}/messages/{messageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable Long conversationId, @PathVariable Long messageId,
            @Valid @RequestBody UpdateMessageRequest request) {
        MessageResponse response = messagingService.updateMessage(conversationId, messageId, request);
        return ResponseEntity.ok(ApiResponse.success("Message updated successfully", response));
    }

    @DeleteMapping("/conversations/{conversationId}/messages/{messageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable Long conversationId, @PathVariable Long messageId) {
        messagingService.deleteMessage(conversationId, messageId);
        return ResponseEntity.ok(ApiResponse.success("Message deleted successfully"));
    }

    @PostMapping("/conversations/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        messagingService.markConversationAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read successfully"));
    }

    @GetMapping("/conversations/{id}/unread/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long id) {
        long count = messagingService.getUnreadCount(id);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", count));
    }

    @GetMapping("/unread/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<Long>> getTotalUnreadCount() {
        long count = messagingService.getTotalUnreadCount();
        return ResponseEntity.ok(ApiResponse.success("Total unread count retrieved successfully", count));
    }
}