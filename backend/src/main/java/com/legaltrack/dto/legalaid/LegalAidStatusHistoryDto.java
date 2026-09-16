package com.legaltrack.dto.legalaid;

import com.legaltrack.enums.LegalAidStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAidStatusHistoryDto {
    private Long id;
    private Long applicationId;
    private LegalAidStatus previousStatus;
    private LegalAidStatus newStatus;
    private String remarks;
    private Long changedById;
    private String changedByName;
    private LocalDateTime changedAt;
}
