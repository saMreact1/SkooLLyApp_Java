package com.samreact.skooLLy.modules.communication.dto;

import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementPriority;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAnnouncementRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private AnnouncementTarget target = AnnouncementTarget.ALL;
    private AnnouncementPriority priority = AnnouncementPriority.NORMAL;

    private LocalDateTime publishAt;
    private LocalDateTime expiresAt;
    private boolean published = true;
}
