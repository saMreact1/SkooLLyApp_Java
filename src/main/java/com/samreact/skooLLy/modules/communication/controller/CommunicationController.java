package com.samreact.skooLLy.modules.communication.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.modules.communication.dto.*;
import com.samreact.skooLLy.modules.communication.entity.enums.AnnouncementTarget;
import com.samreact.skooLLy.modules.communication.service.CommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
public class CommunicationController {
    private final CommunicationService communicationService;

    @PostMapping("/announcements")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {

        AnnouncementResponse announcement = communicationService.createAnnouncement(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement created successfully", announcement));
    }

    @GetMapping("/announcements")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAllAnnouncements() {
        List<AnnouncementResponse> announcements = communicationService.getAllAnnouncements();

        return ResponseEntity.ok(
                ApiResponse.success("Announcements retrieved successfully", announcements));
    }

    @GetMapping("/announcements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getAnnouncementById(
            @PathVariable Long id) {

        AnnouncementResponse announcement = communicationService.getAnnouncementById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Announcement retrieved successfully", announcement));
    }

    @GetMapping("/announcements/target/{target}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAnnouncementsByTarget(
            @PathVariable AnnouncementTarget target) {

        List<AnnouncementResponse> announcements = communicationService.getAnnouncementsByTarget(target);

        return ResponseEntity.ok(
                ApiResponse.success("Announcements retrieved successfully", announcements));
    }

    @GetMapping("/announcements/visible")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getVisibleAnnouncements(
            @RequestParam AnnouncementTarget target) {

        List<AnnouncementResponse> announcements = communicationService.getVisibleAnnouncements(target);

        return ResponseEntity.ok(
                ApiResponse.success("Visible announcements retrieved successfully", announcements));
    }

    @PutMapping("/announcements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnnouncementRequest request) {

        AnnouncementResponse announcement = communicationService.updateAnnouncement(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Announcement updated successfully", announcement));
    }

    @DeleteMapping("/announcements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {
        communicationService.deleteAnnouncement(id);

        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully"));
    }

    @GetMapping("/announcements/published/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Long>> getPublishedAnnouncementCount() {
        long count = communicationService.getPublishedAnnouncementCount();

        return ResponseEntity.ok(ApiResponse.success("Published announcement count retrieved successfully", count));
    }
}