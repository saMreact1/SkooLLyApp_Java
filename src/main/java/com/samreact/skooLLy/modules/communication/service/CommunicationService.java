package com.samreact.skooLLy.modules.communication.service;

import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;

import java.util.List;

public interface CommunicationService {
    AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request);

    AnnouncementResponse getAnnouncementById(Long id);

    List<AnnouncementResponse> getAllAnnouncements();

    List<AnnouncementResponse> getAnnouncementsByTarget(AnnouncementTarget target);

    List<AnnouncementResponse> getVisibleAnnouncements(AnnouncementTarget target);

    AnnouncementResponse updateAnnouncement(Long id, UpdateAnnouncementRequest request);

    void deleteAnnouncement(Long id);

    long getPublishedAnnouncementCount();
}