package com.legaltrack.dto.casefile;

import com.legaltrack.enums.HearingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHearingRequest {

    @NotNull(message = "Hearing date is required")
    private LocalDate hearingDate;

    private String hearingTime;
    private Long courtId;
    private String judgeName;
    private String purpose;
    private String stage;

    @Builder.Default
    private HearingStatus status = HearingStatus.SCHEDULED;

    private String notes;
    private LocalDate nextHearingDate;
}
