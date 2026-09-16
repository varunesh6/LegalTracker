package com.legaltrack.dto.legalaid;

import com.legaltrack.enums.LegalAidCategory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAidPreCheckRequest {

    @NotNull(message = "Category is required")
    private LegalAidCategory category;

    @NotNull(message = "Annual income is required")
    private BigDecimal annualIncome;

    private Long stateId;
    private Long districtId;
    private String employmentStatus;
    private String caseType;
    private String reasonForAssistance;
}
