package com.samreact.skooLLy.modules.communication.repository;

import com.samreact.skooLLy.modules.communication.entity.Conversation;
import com.samreact.skooLLy.modules.communication.entity.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findAllBySchoolIdAndParticipantsUserIdOrderByUpdatedAtDesc(Long schoolId, Long userId);

    List<Conversation> findAllBySchoolIdAndTypeAndParticipantsUserIdOrderByUpdatedAtDesc(
            Long schoolId, ConversationType type, Long userId);

    @Query("SELECT c FROM Conversation c " +
            "JOIN c.participants p1 " +
            "JOIN c.participants p2 " +
            "WHERE c.type = 'DIRECT' " +
            "AND c.school.id = :schoolId " +
            "AND p1.user.id = :userId1 " +
            "AND p2.user.id = :userId2 " +
            "AND p1.leftAt IS NULL " +
            "AND p2.leftAt IS NULL")
    Optional<Conversation> findDirectConversation(
            @Param("schoolId") Long schoolId,
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    Optional<Conversation> findByIdAndSchoolId(Long id, Long schoolId);

    long countBySchoolIdAndParticipantsUserId(Long schoolId, Long userId);
}