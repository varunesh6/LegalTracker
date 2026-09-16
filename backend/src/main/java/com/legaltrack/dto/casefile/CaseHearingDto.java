package com.legaltrack.dto.casefile;

import com.legaltrack.enums.HearingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseHearingDto {
    private Long id;
    private Long caseId;
    private String caseTitle;
    private String caseNumber;
    private String cnrNumber;
    private LocalDate hearingDate;
    private String hearingTime;
    private Long courtId;
    private String courtName;
    private String judgeName;
    private String purpose;
    private String stage;
    private HearingStatus status;
    private String notes;
    private LocalDate nextHearingDate;
    private String source;
    private LocalDateTime createdAt;
}
