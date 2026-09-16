package com.legaltrack.dto.tracked;

import com.legaltrack.dto.casefile.CaseFileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackedCaseDto {
    private Long id;
    private Long userId;
    private Long caseId;
    private String nickname;
    private Boolean notificationsEnabled;
    private LocalDateTime trackedAt;
    private LocalDateTime lastViewedAt;
    private CaseFileDto caseFile;
}
