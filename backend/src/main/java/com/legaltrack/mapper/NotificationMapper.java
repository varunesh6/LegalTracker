package com.legaltrack.mapper;

import com.legaltrack.dto.notification.NotificationDto;
import com.legaltrack.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notif) {
        if (notif == null) return null;

        return NotificationDto.builder()
                .id(notif.getId())
                .userId(notif.getUser().getId())
                .type(notif.getType())
                .title(notif.getTitle())
                .message(notif.getMessage())
                .referenceType(notif.getReferenceType())
                .referenceId(notif.getReferenceId())
                .actionUrl(notif.getActionUrl())
                .isRead(notif.getIsRead())
                .createdAt(notif.getCreatedAt())
                .build();
    }
}
