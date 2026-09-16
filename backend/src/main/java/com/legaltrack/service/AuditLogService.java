package com.legaltrack.service;

import com.legaltrack.dto.admin.AuditLogDto;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.entity.User;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logAction(User actor, String action, String entityType, Long entityId, String oldValue, String newValue);
    PagedResponse<AuditLogDto> getAuditLogs(Pageable pageable);
    PagedResponse<AuditLogDto> getAuditLogsByUser(Long userId, Pageable pageable);
}
