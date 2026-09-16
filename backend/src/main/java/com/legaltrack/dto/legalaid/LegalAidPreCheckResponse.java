package com.legaltrack.dto.legalaid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAidPreCheckResponse {

    private boolean eligible;
    private String status; // POTENTIALLY_ELIGIBLE, REQUIRES_MANUAL_VERIFICATION, NOT_ELIGIBLE
    private String categoryEvaluated;
    private BigDecimal incomeLimit;
    private BigDecimal applicantIncome;
    private String message;
    private String disclaimer;
    private List<String> requiredDocuments;
}
