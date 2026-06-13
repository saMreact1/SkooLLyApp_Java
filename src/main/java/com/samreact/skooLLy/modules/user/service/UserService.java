package com.samreact.skooLLy.modules.user.service;

import com.samreact.skooLLy.modules.user.dto.*;

import java.util.List;

public interface UserService {
    AuthResponseDTO completeRegistration(CompleteRegistrationRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
    UserResponseDTO getUserById(Long id);
    CheckEmailAndSchoolResponseDTO checkEmailAndSchool(CheckEmailAndSchoolRequestDTO request);
    List<UserSearchResult> searchUsers(String query);
    List<UserResponseDTO> getUsersInSchool(Long schoolId);
    Long getSchoolIdByEmail(String email);
}
