package com.legaltrack.mapper;

import com.legaltrack.dto.admin.AuditLogDto;
import com.legaltrack.dto.admin.CaseSyncLogDto;
import com.legaltrack.entity.AuditLog;
import com.legaltrack.entity.CaseSyncLog;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {

    public AuditLogDto toDto(AuditLog log) {
        if (log == null) return null;

        return AuditLogDto.builder()
                .id(log.getId())
                .actorUserId(log.getActorUser() != null ? log.getActorUser().getId() : null)
                .actorName(log.getActorUser() != null ? log.getActorUser().getName() : "SYSTEM")
                .actorEmail(log.getActorUser() != null ? log.getActorUser().getEmail() : "system@legaltrack.local")
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .timestamp(log.getTimestamp())
                .build();
    }

    public CaseSyncLogDto toSyncLogDto(CaseSyncLog syncLog) {
        if (syncLog == null) return null;

        return CaseSyncLogDto.builder()
                .id(syncLog.getId())
                .caseId(syncLog.getCaseFile().getId())
                .caseTitle(syncLog.getCaseFile().getTitle())
                .cnrNumber(syncLog.getCaseFile().getCnrNumber())
                .startedAt(syncLog.getStartedAt())
                .completedAt(syncLog.getCompletedAt())
                .status(syncLog.getStatus())
                .recordsUpdated(syncLog.getRecordsUpdated())
                .errorMessage(syncLog.getErrorMessage())
                .source(syncLog.getSource())
                .build();
    }
}
