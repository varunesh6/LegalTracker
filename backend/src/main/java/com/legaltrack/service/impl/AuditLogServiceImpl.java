package com.legaltrack.service.impl;

import com.legaltrack.dto.admin.AuditLogDto;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.entity.AuditLog;
import com.legaltrack.entity.User;
import com.legaltrack.mapper.AuditMapper;
import com.legaltrack.repository.AuditLogRepository;
import com.legaltrack.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditMapper auditMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(User actor, String action, String entityType, Long entityId, String oldValue, String newValue) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .actorUser(actor)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception ex) {
            log.error("Failed to persist audit log: {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogDto> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAll(pageable);
        return PagedResponse.<AuditLogDto>builder()
                .content(page.getContent().stream().map(auditMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogDto> getAuditLogsByUser(Long userId, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByActorUserId(userId, pageable);
        return PagedResponse.<AuditLogDto>builder()
                .content(page.getContent().stream().map(auditMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
