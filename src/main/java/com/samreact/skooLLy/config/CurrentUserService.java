package com.samreact.skooLLy.config;

import com.samreact.skooLLy.exception.BusinessException;
import com.samreact.skooLLy.modules.user.entity.User;
import com.samreact.skooLLy.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    /**
     * Returns the currently authenticated user.
     * Throws an exception if no user is authenticated.
     */
    @Transactional
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(
                    "No authenticated user found",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        "Authenticated user not found in database",
                        HttpStatus.UNAUTHORIZED
                ));
    }

    /**
     * Returns the school ID of the currently authenticated user.
     * This is the key method for multi-tenancy —
     * every query will be scoped to this school ID.
     */
    @Transactional
    public Long getCurrentSchoolId() {
        return getCurrentUser().getSchool().getId();
    }

    /**
     * Returns the currently authenticated user's ID.
     */
    @Transactional
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Checks if the current user has a specific role.
     * Usage: currentUserService.hasRole("ADMIN")
     */
    @Transactional
    public boolean hasRole(String role) {
        return getCurrentUser()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_" + role));
    }
}