package com.samreact.skooLLy.modules.communication.service.implementation;

import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.*;
import com.samreact.skooLLy.modules.communication.entity.enums.ConversationType;
import com.samreact.skooLLy.modules.communication.entity.enums.MessageType;
import com.samreact.skooLLy.modules.communication.repository.ConversationParticipantRepository;
import com.samreact.skooLLy.modules.communication.repository.ConversationRepository;
import com.samreact.skooLLy.modules.communication.repository.MessageRepository;
import com.samreact.skooLLy.modules.communication.service.MessagingService;
import com.samreact.skooLLy.modules.user.entity.User;
import com.samreact.skooLLy.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getType() == ConversationType.DIRECT) {
            if (request.getParticipantIds().size() != 1) {
                throw new BusinessException("Direct conversation must have exactly one other participant", HttpStatus.BAD_REQUEST);
            }
            Long otherId = request.getParticipantIds().get(0);
            var existing = conversationRepository.findDirectConversation(schoolId, userId, otherId);
            if (existing.isPresent()) {
                return mapToConversationResponse(existing.get(), userId);
            }
        }

        Conversation conversation = Conversation.builder()
                .school(currentUser.getSchool())
                .title(request.getTitle())
                .type(request.getType())
                .createdBy(currentUser)
                .build();

        Conversation saved = conversationRepository.save(conversation);

        List<Long> allParticipantIds = new ArrayList<>(request.getParticipantIds());
        if (!allParticipantIds.contains(userId)) {
            allParticipantIds.add(userId);
        }

        for (Long pid : allParticipantIds) {
            User participant = userRepository.findById(pid)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", pid));
            ConversationParticipant cp = ConversationParticipant.builder()
                    .conversation(saved)
                    .user(participant)
                    .build();
            participantRepository.save(cp);
        }

        saved.getParticipants().clear();
        saved.getParticipants().addAll(participantRepository.findByConversation(saved));
        log.info("Conversation created: id={}, type={}", saved.getId(), saved.getType());

        return mapToConversationResponse(saved, userId);
    }

    @Override
    @Transactional
    public ConversationResponse getConversationById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        if (!isParticipant(conversation, userId)) {
            throw new BusinessException("You are not a participant in this conversation", HttpStatus.FORBIDDEN);
        }

        return mapToConversationResponse(conversation, userId);
    }

    @Override
    @Transactional
    public List<ConversationResponse> getUserConversations() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        return conversationRepository
                .findAllBySchoolIdAndParticipantsUserIdOrderByUpdatedAtDesc(schoolId, userId)
                .stream()
                .map(c -> mapToConversationResponse(c, userId))
                .toList();
    }

    @Override
    @Transactional
    public List<ConversationResponse> getUserConversationsByType(ConversationType type) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        return conversationRepository
                .findAllBySchoolIdAndTypeAndParticipantsUserIdOrderByUpdatedAtDesc(schoolId, type, userId)
                .stream()
                .map(c -> mapToConversationResponse(c, userId))
                .toList();
    }

    @Override
    @Transactional
    public ConversationResponse updateConversation(Long id, UpdateConversationRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        if (!isParticipant(conversation, userId)) {
            throw new BusinessException("You are not a participant in this conversation", HttpStatus.FORBIDDEN);
        }

        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }

        Conversation updated = conversationRepository.save(conversation);
        return mapToConversationResponse(updated, userId);
    }

    @Override
    @Transactional
    public void deleteConversation(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        if (!conversation.getCreatedBy().getId().equals(userId)) {
            throw new BusinessException("Only the creator can delete this conversation", HttpStatus.FORBIDDEN);
        }

        conversationRepository.delete(conversation);
        log.info("Conversation deleted: id={}", id);
    }

    @Override
    @Transactional
    public void leaveConversation(Long conversationId) {
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ConversationParticipant participant = participantRepository
                .findByConversationAndUser(conversation, user)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "userId", userId));

        participant.setLeftAt(LocalDateTime.now());
        participantRepository.save(participant);
        log.info("User {} left conversation {}", userId, conversationId);
    }

    @Override
    @Transactional
    public void addParticipants(Long conversationId, List<Long> userIds) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long currentUserId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(conversationId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        if (!isParticipant(conversation, currentUserId)) {
            throw new BusinessException("You are not a participant in this conversation", HttpStatus.FORBIDDEN);
        }

        for (Long uid : userIds) {
            if (!participantRepository.existsByConversationAndUser(conversation, userRepository.getReferenceById(uid))) {
                User user = userRepository.findById(uid)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", uid));
                ConversationParticipant cp = ConversationParticipant.builder()
                        .conversation(conversation)
                        .user(user)
                        .build();
                participantRepository.save(cp);
            }
        }
        log.info("Participants added to conversation {}: {}", conversationId, userIds);
    }

    @Override
    @Transactional
    public void removeParticipant(Long conversationId, Long userId) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long currentUserId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(conversationId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        if (!conversation.getCreatedBy().getId().equals(currentUserId)) {
            throw new BusinessException("Only the creator can remove participants", HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ConversationParticipant participant = participantRepository
                .findByConversationAndUser(conversation, user)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "userId", userId));

        participantRepository.delete(participant);
        log.info("User {} removed from conversation {}", userId, conversationId);
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(CreateMessageRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        Long schoolId = currentUserService.getCurrentSchoolId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(request.getConversationId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));

        if (!isParticipant(conversation, userId)) {
            throw new BusinessException("You are not a participant in this conversation", HttpStatus.FORBIDDEN);
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : MessageType.TEXT)
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .attachmentSize(request.getAttachmentSize())
                .build();

        Message saved = messageRepository.save(message);
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        sendMessageNotification(saved, conversation);
        log.info("Message sent: id={} in conversation={}", saved.getId(), conversation.getId());

        return mapToMessageResponse(saved);
    }

    @Override
    @Transactional
    public MessageResponse getMessageById(Long conversationId, Long messageId) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(conversationId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        if (!isParticipant(conversation, userId)) {
            throw new BusinessException("You are not a participant in this conversation", HttpStatus.FORBIDDEN);
        }

        Message message = messageRepository.findByIdAndConversation(messageId, conversation)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        return mapToMessageResponse(message);
    }

    @Override
    @Transactional
    public List<MessageResponse> getConversationMessages(Long conversationId, int page, int size) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(conversationId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        if (!isParticipant(conversation, userId)) {
            throw new BusinessException("You are not a participant in this conversation", HttpStatus.FORBIDDEN);
        }

        return messageRepository
                .findByConversationNotDeletedOrderByCreatedAtDesc(conversation)
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::mapToMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse updateMessage(Long conversationId, Long messageId, UpdateMessageRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(conversationId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        Message message = messageRepository.findByIdAndConversation(messageId, conversation)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        if (!message.getSender().getId().equals(userId)) {
            throw new BusinessException("You can only edit your own messages", HttpStatus.FORBIDDEN);
        }

        if (request.getContent() != null) {
            message.setContent(request.getContent());
            message.setEdited(true);
        }

        Message updated = messageRepository.save(message);
        return mapToMessageResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMessage(Long conversationId, Long messageId) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository
                .findByIdAndSchoolId(conversationId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        Message message = messageRepository.findByIdAndConversation(messageId, conversation)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        if (!message.getSender().getId().equals(userId)
                && !conversation.getCreatedBy().getId().equals(userId)) {
            throw new BusinessException("You can only delete your own messages", HttpStatus.FORBIDDEN);
        }

        message.setDeleted(true);
        message.setContent("This message has been deleted");
        messageRepository.save(message);
        log.info("Message soft-deleted: id={} in conversation={}", messageId, conversationId);
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long conversationId) {
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ConversationParticipant participant = participantRepository
                .findByConversationAndUser(conversation, user)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "userId", userId));

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    @Override
    @Transactional
    public long getUnreadCount(Long conversationId) {
        Long userId = currentUserService.getCurrentUserId();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ConversationParticipant participant = participantRepository
                .findByConversationAndUser(conversation, user)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "userId", userId));

        LocalDateTime lastRead = participant.getLastReadAt();
        if (lastRead == null) {
            return messageRepository.countByConversationAndDeletedFalseAndSenderNot(conversation, user);
        }
        return messageRepository.countByConversationAndCreatedAtAfterAndSenderNot(conversation, lastRead, user);
    }

    @Override
    @Transactional
    public long getTotalUnreadCount() {
        Long userId = currentUserService.getCurrentUserId();

        List<Conversation> conversations = conversationRepository
                .findAllBySchoolIdAndParticipantsUserIdOrderByUpdatedAtDesc(
                        currentUserService.getCurrentSchoolId(), userId);

        return conversations.stream()
                .mapToLong(c -> getUnreadCount(c.getId()))
                .sum();
    }

    private boolean isParticipant(Conversation conversation, Long userId) {
        return conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId) && p.getLeftAt() == null);
    }
    private ConversationResponse mapToConversationResponse(Conversation conversation, Long currentUserId) {
        var participants = conversation.getParticipants().stream()
                .filter(p -> p.getLeftAt() == null)
                .map(p -> ConversationParticipantResponse.builder()
                        .userId(p.getUser().getId())
                        .firstName(p.getUser().getFirstName())
                        .lastName(p.getUser().getLastName())
                        .email(p.getUser().getEmail())
                        .role(p.getUser().getRole().name())
                        .muted(p.isMuted())
                        .lastReadAt(p.getLastReadAt())
                        .joinedAt(p.getJoinedAt())
                        .build())
                .toList();

        List<Message> recentMessages = messageRepository
                .findByConversationNotDeletedOrderByCreatedAtDesc(conversation);

        MessageResponse lastMessage = null;
        if (!recentMessages.isEmpty()) {
            lastMessage = mapToMessageResponse(recentMessages.get(0));
        }

        LocalDateTime lastRead = conversation.getParticipants().stream()
                .filter(p -> p.getUser().getId().equals(currentUserId))
                .findFirst()
                .map(ConversationParticipant::getLastReadAt)
                .orElse(null);

        User currentUser = userRepository.findById(currentUserId).orElse(null);

        int unreadCount = 0;
        if (currentUser != null) {
            if (lastRead != null) {
                unreadCount = (int) messageRepository
                        .countByConversationAndCreatedAtAfterAndSenderNot(conversation, lastRead, currentUser);
            } else {
                unreadCount = (int) messageRepository
                        .countByConversationAndDeletedFalseAndSenderNot(conversation, currentUser);
            }
        }

        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .type(conversation.getType())
                .createdById(conversation.getCreatedBy().getId())
                .createdByName(conversation.getCreatedBy().getFirstName()
                        + " " + conversation.getCreatedBy().getLastName())
                .participants(participants)
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName()
                        + " " + message.getSender().getLastName())
                .senderRole(message.getSender().getRole().name())
                .content(message.getContent())
                .type(message.getType())
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentName(message.getAttachmentName())
                .attachmentSize(message.getAttachmentSize())
                .edited(message.isEdited())
                .deleted(message.isDeleted())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    private void sendMessageNotification(Message message, Conversation conversation) {
        MessageNotification notification = MessageNotification.builder()
                .type("NEW_MESSAGE")
                .conversationId(conversation.getId())
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName()
                        + " " + message.getSender().getLastName())
                .content(message.getContent())
                .messageType(message.getType())
                .createdAt(message.getCreatedAt())
                .build();

        String destination = "/topic/conversations." + conversation.getId();
        messagingTemplate.convertAndSend(destination, notification);
        log.debug("Sent message notification to {}", destination);
    }
}