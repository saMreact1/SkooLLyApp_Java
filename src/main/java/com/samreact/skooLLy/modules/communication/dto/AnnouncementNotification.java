package com.samreact.skooLLy.modules.communication.dto;

import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementPriority;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnnouncementNotification {
    private String type;
    private Long announcementId;
    private String title;
    private String content;
    private AnnouncementTarget target;
    private AnnouncementPriority priority;
    private String postedByName;
    private LocalDateTime createdAt;
}