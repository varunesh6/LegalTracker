package com.legaltrack.service.impl;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.notification.NotificationDto;
import com.legaltrack.entity.Notification;
import com.legaltrack.entity.User;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.NotificationMapper;
import com.legaltrack.repository.NotificationRepository;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void createNotification(User user, NotificationType type, String title, String message, String refType, Long refId, String actionUrl) {
        if (user == null) return;

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceType(refType)
                .referenceId(refId)
                .actionUrl(actionUrl)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = notificationMapper.toDto(saved);

        try {
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/notifications",
                    dto
            );
        } catch (Exception ex) {
            log.debug("Could not push WebSocket notification: {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PagedResponse.<NotificationDto>builder()
                .content(page.getContent().stream().map(notificationMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Cannot modify notification of another user");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
