package com.legaltrack.dto.tracked;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackCaseRequest {

    @NotNull(message = "Case ID is required")
    private Long caseId;

    private String nickname;

    @Builder.Default
    private Boolean notificationsEnabled = true;
}
