package com.legaltrack.dto.casefile;

import com.legaltrack.enums.CaseStage;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.enums.EngagementType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaseRequest {

    @NotBlank(message = "Case title is required")
    private String title;

    @NotBlank(message = "Case type is required")
    private String caseType;

    private String caseCategory;
    private Long clientId;
    private Long lawyerId;
    private Long courtId;
    private Long stateId;
    private Long districtId;
    private Long courtComplexId;
    private String cnrNumber;
    private String caseNumber;
    private String filingNumber;
    private LocalDate filingDate;
    private String registrationNumber;
    private LocalDate registrationDate;
    private String firNumber;
    private Integer firYear;
    private Long policeStationId;
    private String act;
    private String section;
    private String matterDescription;

    @Builder.Default
    private CaseStatus status = CaseStatus.PENDING;

    @Builder.Default
    private CaseStage stage = CaseStage.APPEARANCE;

    @Builder.Default
    private EngagementType engagementType = EngagementType.PRIVATE_LAWYER;

    private LocalDate nextHearingDate;
    private List<CasePartyDto> parties;
    private List<CaseAdvocateDto> advocates;
}
