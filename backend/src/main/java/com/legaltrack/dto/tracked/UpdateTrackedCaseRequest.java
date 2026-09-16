package com.legaltrack.dto.tracked;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrackedCaseRequest {
    private String nickname;
    private Boolean notificationsEnabled;
}
