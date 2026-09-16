package com.legaltrack.dto.lawyer;

import com.legaltrack.enums.LawyerAvailabilityStatus;
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
public class LawyerAvailabilityDto {
    private Long id;
    private Long lawyerId;
    private LawyerAvailabilityStatus status;
    private LocalDate availableFrom;
    private LocalDate availableUntil;
    private LocalDateTime updatedAt;
}
