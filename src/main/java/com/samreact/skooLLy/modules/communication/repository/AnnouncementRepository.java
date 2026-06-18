package com.samreact.skooLLy.modules.communication.repository;

import com.samreact.skooLLy.modules.communication.entity.Announcement;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    Page<Announcement> findAllBySchoolIdOrderByCreatedAtDesc(Long schoolId, Pageable pageable);

    List<Announcement> findAllBySchoolIdAndTargetOrderByCreatedAtDesc(
            Long schoolId, AnnouncementTarget target);

    Page<Announcement> findAllBySchoolIdAndTargetOrderByCreatedAtDesc(
            Long schoolId, AnnouncementTarget target, Pageable pageable);

    Optional<Announcement> findByIdAndSchoolId(Long id, Long schoolId);

    // Announcements visible to a specific role
    // Returns ALL announcements + role-specific ones
    @Query("SELECT a FROM Announcement a WHERE " +
            "a.school.id = :schoolId AND " +
            "a.published = true AND " +
            "(a.target = 'ALL' OR a.target = :target) " +
            "ORDER BY a.createdAt DESC")
    List<Announcement> findVisibleAnnouncements(
            @Param("schoolId") Long schoolId,
            @Param("target") AnnouncementTarget target);

    @Query("SELECT a FROM Announcement a WHERE a.school.id = :schoolId AND a.published = true " +
           "AND (a.target = :target OR a.target = 'ALL') " +
           "AND (a.publishAt IS NULL OR a.publishAt <= CURRENT_TIMESTAMP) " +
           "AND (a.expiresAt IS NULL OR a.expiresAt >= CURRENT_TIMESTAMP) " +
           "ORDER BY a.createdAt DESC")
    Page<Announcement> findVisibleAnnouncements(
            @Param("schoolId") Long schoolId,
            @Param("target") AnnouncementTarget target,
            Pageable pageable);

    long countBySchoolIdAndPublished(Long schoolId, boolean published);
}
