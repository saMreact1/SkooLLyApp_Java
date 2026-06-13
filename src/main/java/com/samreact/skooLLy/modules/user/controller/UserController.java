package com.samreact.skooLLy.modules.user.controller;

import com.samreact.skooLLy.common.response.ApiResponse;
import com.samreact.skooLLy.config.CurrentUserService;
import com.samreact.skooLLy.modules.user.dto.UserResponseDTO;
import com.samreact.skooLLy.modules.user.dto.UserSearchResult;
import com.samreact.skooLLy.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserSearchResult>>> searchUsers(
            @RequestParam("q") String query) {
        log.debug("Searching users with query: {}", query);
        List<UserSearchResult> results = userService.searchUsers(query);
        return ResponseEntity.ok(ApiResponse.success("Search completed", results));
    }

    @GetMapping("/school")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersInSchool() {
        Long schoolId = currentUserService.getCurrentSchoolId();
        List<UserResponseDTO> users = userService.getUsersInSchool(schoolId);
        
        return ResponseEntity.ok(ApiResponse.success("Users in school retrieved successfully", users));
    }
}
