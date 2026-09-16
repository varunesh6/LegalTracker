package com.legaltrack.dto.lawyer;

import com.legaltrack.enums.LawyerAvailabilityStatus;
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
public class UpdateAvailabilityRequest {

    @NotNull(message = "Status is required")
    private LawyerAvailabilityStatus status;

    private LocalDate availableFrom;
    private LocalDate availableUntil;
}
