package com.samreact.skooLLy.modules.communication.dto;

import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementPriority;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateAnnouncementRequest {
    private String title;
    private String content;
    private AnnouncementTarget target;
    private AnnouncementPriority priority;
    private Boolean published;
    private LocalDateTime publishAt;
    private LocalDateTime expiresAt;
}
