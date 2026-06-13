package com.samreact.skooLLy.modules.communication.repository;

import com.samreact.skooLLy.modules.communication.entity.Announcement;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    List<Announcement> findAllBySchoolIdAndTargetOrderByCreatedAtDesc(
            Long schoolId, AnnouncementTarget target);

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

    long countBySchoolIdAndPublished(Long schoolId, boolean published);
}
