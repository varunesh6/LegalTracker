package com.legaltrack.controller;

import com.legaltrack.dto.admin.CaseSyncLogDto;
import com.legaltrack.dto.admin.SystemStatsDto;
import com.legaltrack.dto.auth.UserProfileDto;
import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.enums.RoleType;
import com.legaltrack.enums.UserStatus;
import com.legaltrack.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "Endpoints for platform administration, system stats, user control, and sync logs")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @Operation(summary = "Get high-level platform statistics and metrics")
    public ResponseEntity<ApiResponse<SystemStatsDto>> getSystemStats() {
        SystemStatsDto stats = adminService.getSystemStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users")
    @Operation(summary = "Get paginated list of registered users with optional role filtering")
    public ResponseEntity<ApiResponse<PagedResponse<UserProfileDto>>> getUsers(
            @RequestParam(required = false) RoleType role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<UserProfileDto> users = adminService.getUsers(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Update user account status (ACTIVE, SUSPENDED, PENDING_VERIFICATION, DEACTIVATED)")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam UserStatus status) {
        adminService.updateUserStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success("User status updated to " + status, null));
    }

    @GetMapping("/sync-logs")
    @Operation(summary = "Get court sync logs history")
    public ResponseEntity<ApiResponse<PagedResponse<CaseSyncLogDto>>> getSyncLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startedAt").descending());
        PagedResponse<CaseSyncLogDto> logs = adminService.getSyncLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
