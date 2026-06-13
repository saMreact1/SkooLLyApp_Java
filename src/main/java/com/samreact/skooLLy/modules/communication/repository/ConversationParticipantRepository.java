package com.samreact.skooLLy.modules.communication.repository;

import com.samreact.skooLLy.modules.communication.entity.Conversation;
import com.samreact.skooLLy.modules.communication.entity.ConversationParticipant;
import com.samreact.skooLLy.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    Optional<ConversationParticipant> findByConversationAndUser(Conversation conversation, User user);

    List<ConversationParticipant> findByConversation(Conversation conversation);

    List<ConversationParticipant> findByUser(User user);

    boolean existsByConversationAndUser(Conversation conversation, User user);

    @Modifying
    @Transactional
    @Query("UPDATE ConversationParticipant cp SET cp.lastReadAt = :readAt " +
            "WHERE cp.conversation.id = :conversationId AND cp.user.id = :userId")
    void updateLastReadAt(@Param("conversationId") Long conversationId,
                          @Param("userId") Long userId,
                          @Param("readAt") java.time.LocalDateTime readAt);

    @Modifying
    @Transactional
    @Query("UPDATE ConversationParticipant cp SET cp.leftAt = :leftAt " +
            "WHERE cp.conversation.id = :conversationId AND cp.user.id = :userId")
    void leaveConversation(@Param("conversationId") Long conversationId,
                           @Param("userId") Long userId,
                           @Param("leftAt") java.time.LocalDateTime leftAt);

    long countByConversation(Conversation conversation);
}