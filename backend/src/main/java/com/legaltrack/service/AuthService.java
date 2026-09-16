package com.legaltrack.service;

import com.legaltrack.dto.auth.*;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse registerClient(RegisterClientRequest registerRequest);
    AuthResponse registerLawyer(RegisterLawyerRequest registerRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    void logout(Long userId);
    UserProfileDto getCurrentUserProfile();
    void changePassword(ChangePasswordRequest request);
}
