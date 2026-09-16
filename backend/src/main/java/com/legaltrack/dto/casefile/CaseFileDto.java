package com.legaltrack.dto.casefile;

import com.legaltrack.enums.CaseStage;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.enums.EngagementType;
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
public class CaseFileDto {

    private Long id;
    private String internalReferenceId;
    private Long clientId;
    private String clientName;
    private Long lawyerId;
    private String lawyerName;
    private Long courtId;
    private String courtName;
    private String courtComplexName;
    private Long stateId;
    private String stateName;
    private Long districtId;
    private String districtName;
    private String caseType;
    private String caseCategory;
    private String title;
    private String cnrNumber;
    private String caseNumber;
    private String filingNumber;
    private LocalDate filingDate;
    private String registrationNumber;
    private LocalDate registrationDate;
    private String firNumber;
    private Integer firYear;
    private String policeStationName;
    private String act;
    private String section;
    private CaseStatus status;
    private CaseStage stage;
    private String matterDescription;
    private EngagementType engagementType;
    private Boolean isDemoData;
    private LocalDate nextHearingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
