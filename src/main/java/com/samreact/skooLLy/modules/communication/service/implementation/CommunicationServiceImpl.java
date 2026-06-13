package com.samreact.skooLLy.modules.communication.service.implementation;

import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.exception.ResourceNotFoundException;
import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.Announcement;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import com.samreact.skooLLy.modules.communication.repository.AnnouncementRepository;
import com.samreact.skooLLy.modules.communication.service.CommunicationService;
import com.samreact.skooLLy.modules.user.entity.User;
import com.samreact.skooLLy.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();
        Long userId = currentUserService.getCurrentUserId();

        User postedBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Announcement announcement = Announcement.builder()
                .school(postedBy.getSchool())
                .postedBy(postedBy)
                .title(request.getTitle())
                .content(request.getContent())
                .target(request.getTarget())
                .priority(request.getPriority())
                .publishAt(request.getPublishAt())
                .expiresAt(request.getExpiresAt())
                .published(request.isPublished())
                .build();

        Announcement saved = announcementRepository.save(announcement);
        log.info("Announcement created: {} by user: {}", saved.getTitle(), postedBy.getEmail());

        if (saved.getPublished()) {
            sendNotification(saved);
        }

        return mapToAnnouncementResponse(saved);
    }

    @Override
    @Transactional
    public AnnouncementResponse getAnnouncementById(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Announcement announcement = announcementRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        return mapToAnnouncementResponse(announcement);
    }

    @Override
    @Transactional
    public List<AnnouncementResponse> getAllAnnouncements() {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return announcementRepository
                .findAllBySchoolIdOrderByCreatedAtDesc(schoolId)
                .stream()
                .map(this::mapToAnnouncementResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<AnnouncementResponse> getAnnouncementsByTarget(AnnouncementTarget target) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return announcementRepository
                .findAllBySchoolIdAndTargetOrderByCreatedAtDesc(schoolId, target)
                .stream()
                .map(this::mapToAnnouncementResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<AnnouncementResponse> getVisibleAnnouncements(AnnouncementTarget target) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        return announcementRepository
                .findVisibleAnnouncements(schoolId, target)
                .stream()
                .map(this::mapToAnnouncementResponse)
                .toList();
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long id, UpdateAnnouncementRequest request) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Announcement announcement = announcementRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        if (request.getTitle() != null) {
            announcement.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            announcement.setContent(request.getContent());
        }
        if (request.getTarget() != null) {
            announcement.setTarget(request.getTarget());
        }
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        if (request.getPublished() != null) {
            announcement.setPublished(request.getPublished());
        }
        if (request.getPublishAt() != null) {
            announcement.setPublishAt(request.getPublishAt());
        }
        if (request.getExpiresAt() != null) {
            announcement.setExpiresAt(request.getExpiresAt());
        }

        Announcement updated = announcementRepository.save(announcement);
        log.info("Announcement updated: {}", updated.getTitle());

        if (updated.getPublished()) {
            sendNotification(updated);
        }

        return mapToAnnouncementResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        Long schoolId = currentUserService.getCurrentSchoolId();

        Announcement announcement = announcementRepository
                .findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        AnnouncementTarget target = announcement.getTarget();
        announcementRepository.delete(announcement);
        log.info("Announcement deleted: {}", id);

        sendDeleteNotification(id, target);
    }

    @Override
    @Transactional
    public long getPublishedAnnouncementCount() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        return announcementRepository.countBySchoolIdAndPublished(schoolId, true);
    }

    private AnnouncementResponse mapToAnnouncementResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .target(announcement.getTarget())
                .priority(announcement.getPriority())
                .postedByName(announcement.getPostedBy().getFirstName() + " " + announcement.getPostedBy().getLastName())
                .postedById(announcement.getPostedBy().getId())
                .published(announcement.getPublished())
                .publishAt(announcement.getPublishAt())
                .expiresAt(announcement.getExpiresAt())
                .schoolName(announcement.getSchool().getName())
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    private void sendNotification(Announcement announcement) {
        AnnouncementNotification notification = AnnouncementNotification.builder()
                .type("ANNOUNCEMENT_CREATED")
                .announcementId(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .target(announcement.getTarget())
                .priority(announcement.getPriority())
                .postedByName(announcement.getPostedBy().getFirstName() + " " + announcement.getPostedBy().getLastName())
                .createdAt(announcement.getCreatedAt())
                .build();

        String destination = "/topic/announcements." + announcement.getTarget().name();
        messagingTemplate.convertAndSend(destination, notification);
        log.debug("Sent WebSocket notification to {}", destination);
    }

    private void sendDeleteNotification(Long announcementId, AnnouncementTarget target) {
        AnnouncementNotification notification = AnnouncementNotification.builder()
                .type("ANNOUNCEMENT_DELETED")
                .announcementId(announcementId)
                .target(target)
                .build();

        String destination = "/topic/announcements." + target.name();
        messagingTemplate.convertAndSend(destination, notification);
        log.debug("Sent WebSocket delete notification to {}", destination);
    }
}