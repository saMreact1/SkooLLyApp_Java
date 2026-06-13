package com.samreact.skooLLy.modules.communication.repository;

import com.samreact.skooLLy.modules.communication.entity.Conversation;
import com.samreact.skooLLy.modules.communication.entity.Message;
import com.samreact.skooLLy.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    List<Message> findByConversationAndCreatedAtAfterOrderByCreatedAtAsc(
            Conversation conversation, LocalDateTime after);

    Optional<Message> findByIdAndConversation(Long id, Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation " +
            "AND m.deleted = false " +
            "ORDER BY m.createdAt DESC")
    List<Message> findByConversationNotDeletedOrderByCreatedAtDesc(Conversation conversation);

    long countByConversationAndDeletedFalse(Conversation conversation);

    long countByConversationAndCreatedAtAfter(Conversation conversation, LocalDateTime after);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation " +
            "AND m.deleted = false AND m.sender <> :sender")
    long countByConversationAndDeletedFalseAndSenderNot(
            @Param("conversation") Conversation conversation,
            @Param("sender") User sender);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation " +
            "AND m.createdAt > :after AND m.deleted = false AND m.sender <> :sender")
    long countByConversationAndCreatedAtAfterAndSenderNot(
            @Param("conversation") Conversation conversation,
            @Param("after") LocalDateTime after,
            @Param("sender") User sender);
}