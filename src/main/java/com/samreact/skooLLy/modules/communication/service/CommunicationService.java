package com.samreact.skooLLy.modules.communication.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;

public interface CommunicationService {
    AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request);

    AnnouncementResponse getAnnouncementById(Long id);

    PagedResponse<AnnouncementResponse> getAllAnnouncements(int page, int size);

    PagedResponse<AnnouncementResponse> getAnnouncementsByTarget(AnnouncementTarget target, int page, int size);

    PagedResponse<AnnouncementResponse> getVisibleAnnouncements(AnnouncementTarget target, int page, int size);

    AnnouncementResponse updateAnnouncement(Long id, UpdateAnnouncementRequest request);

    void deleteAnnouncement(Long id);

    long getPublishedAnnouncementCount();
}