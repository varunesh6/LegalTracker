package com.legaltrack.dto.casefile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseTimelineResponse {
    private Long caseId;
    private String caseTitle;
    private String cnrNumber;
    private String caseNumber;
    private String currentStage;
    private String currentStatus;
    private List<CaseEventDto> events;
    private List<StageProgressDto> stageProgression;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageProgressDto {
        private String stageName;
        private boolean completed;
        private boolean current;
        private String date;
    }
}
