package com.legaltrack.controller;

import com.legaltrack.dto.auth.*;
import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, JWT token refresh and profile management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register/client")
    @Operation(summary = "Register new Client user account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerClient(@Valid @RequestBody RegisterClientRequest request) {
        AuthResponse response = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Client registered successfully", response));
    }

    @PostMapping("/register/lawyer")
    @Operation(summary = "Register new Lawyer user account")
    public ResponseEntity<ApiResponse<AuthResponse>> registerLawyer(@Valid @RequestBody RegisterLawyerRequest request) {
        AuthResponse response = authService.registerLawyer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Lawyer registered successfully", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh expired JWT access token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate refresh token")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser() {
        UserProfileDto profile = authService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved", profile));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change authenticated user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }
}
