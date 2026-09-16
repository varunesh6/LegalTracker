package com.legaltrack.dto.admin;

import com.legaltrack.enums.SyncStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseSyncLogDto {
    private Long id;
    private Long caseId;
    private String caseTitle;
    private String cnrNumber;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private SyncStatus status;
    private Integer recordsUpdated;
    private String errorMessage;
    private String source;
}
