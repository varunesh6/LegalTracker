package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.notification.NotificationDto;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Endpoints for user notifications and alerts")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated notifications for the current authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationDto>>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Long currentUserId = securityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<NotificationDto> response = notificationService.getUserNotifications(currentUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unread notifications list")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications() {
        Long currentUserId = securityUtils.getCurrentUserId();
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unread notification count badge")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long currentUserId = securityUtils.getCurrentUserId();
        Long count = notificationService.getUnreadCount(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        notificationService.markAsRead(notificationId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long currentUserId = securityUtils.getCurrentUserId();
        notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
