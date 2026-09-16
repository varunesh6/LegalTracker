package com.legaltrack.service;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.notification.NotificationDto;
import com.legaltrack.entity.User;
import com.legaltrack.enums.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, NotificationType type, String title, String message, String refType, Long refId, String actionUrl);
    PagedResponse<NotificationDto> getUserNotifications(Long userId, Pageable pageable);
    List<NotificationDto> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    Long getUnreadCount(Long userId);
}
