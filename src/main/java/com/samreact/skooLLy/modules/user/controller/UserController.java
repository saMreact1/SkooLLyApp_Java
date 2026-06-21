package com.samreact.skooLLy.modules.user.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.modules.user.dto.UpdateProfileRequest;
import com.samreact.skooLLy.modules.user.dto.UserResponseDTO;
import com.samreact.skooLLy.modules.user.dto.UserSearchResult;
import com.samreact.skooLLy.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMyProfile() {
        UserResponseDTO profile = userService.getMyProfile();
        return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateMyProfile(
            @RequestBody UpdateProfileRequest request) {
        UserResponseDTO profile = userService.updateMyProfile(request);
        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", profile));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<ApiResponse<PagedResponse<UserSearchResult>>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserSearchResult> users = userService.searchUsers(q, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/school")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> getUsersInSchool(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserResponseDTO> users = userService.getUsersInSchool(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", users));
    }
}
