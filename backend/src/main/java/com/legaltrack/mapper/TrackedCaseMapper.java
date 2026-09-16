package com.legaltrack.mapper;

import com.legaltrack.dto.tracked.TrackedCaseDto;
import com.legaltrack.entity.TrackedCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackedCaseMapper {

    private final CaseMapper caseMapper;

    public TrackedCaseDto toDto(TrackedCase trackedCase) {
        if (trackedCase == null) return null;

        return TrackedCaseDto.builder()
                .id(trackedCase.getId())
                .userId(trackedCase.getUser().getId())
                .caseId(trackedCase.getCaseFile().getId())
                .nickname(trackedCase.getNickname())
                .notificationsEnabled(trackedCase.getNotificationsEnabled())
                .trackedAt(trackedCase.getTrackedAt())
                .lastViewedAt(trackedCase.getLastViewedAt())
                .caseFile(caseMapper.toDto(trackedCase.getCaseFile()))
                .build();
    }
}
