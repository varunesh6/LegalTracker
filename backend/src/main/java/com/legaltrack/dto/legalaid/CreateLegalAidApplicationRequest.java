package com.legaltrack.dto.legalaid;

import com.legaltrack.enums.LegalAidCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLegalAidApplicationRequest {

    private Long courtId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private LocalDate dateOfBirth;
    private String gender;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "State is required")
    private Long stateId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotBlank(message = "Case type is required")
    private String caseType;

    private String caseStage;

    @NotBlank(message = "Matter description is required")
    private String matterDescription;

    private String opponentInformation;

    @NotNull(message = "Annual income is required")
    private BigDecimal annualIncome;

    @NotBlank(message = "Employment status is required")
    private String employmentStatus;

    @NotNull(message = "Selected category is required")
    private LegalAidCategory selectedCategory;

    private String supportingInformation;
}
