package com.legaltrack.dto.legalaid;

import com.legaltrack.enums.LegalAidStatus;
import com.legaltrack.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAidVerificationRequest {

    @NotNull(message = "New application status is required")
    private LegalAidStatus newStatus;

    private String remarks;
    private Map<Long, VerificationStatus> documentVerifications;
}
