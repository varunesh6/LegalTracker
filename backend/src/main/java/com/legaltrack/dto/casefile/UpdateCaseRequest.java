package com.legaltrack.dto.casefile;

import com.legaltrack.enums.CaseStage;
import com.legaltrack.enums.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCaseRequest {
    private String title;
    private CaseStatus status;
    private CaseStage stage;
    private String matterDescription;
    private LocalDate nextHearingDate;
    private String act;
    private String section;
}
