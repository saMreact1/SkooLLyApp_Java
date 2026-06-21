package com.samreact.skooLLy.modules.user.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.user.dto.*;

public interface UserService {
    AuthResponseDTO completeRegistration(CompleteRegistrationRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getMyProfile();
    UserResponseDTO updateMyProfile(UpdateProfileRequest request);
    CheckEmailAndSchoolResponseDTO checkEmailAndSchool(CheckEmailAndSchoolRequestDTO request);
    PagedResponse<UserSearchResult> searchUsers(String query, int page, int size);
    PagedResponse<UserResponseDTO> getUsersInSchool(int page, int size);
    Long getSchoolIdByEmail(String email);
}
