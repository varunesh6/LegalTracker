package com.legaltrack.dto.legalaid;

import com.legaltrack.enums.LegalAidCategory;
import com.legaltrack.enums.LegalAidStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAidApplicationDto {

    private Long id;
    private String applicationNumber;
    private Long clientId;
    private String clientEmail;
    private Long courtId;
    private String courtName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private Long stateId;
    private String stateName;
    private Long districtId;
    private String districtName;
    private String caseType;
    private String caseStage;
    private String matterDescription;
    private String opponentInformation;
    private BigDecimal annualIncome;
    private String employmentStatus;
    private LegalAidCategory selectedCategory;
    private String supportingInformation;
    private LegalAidStatus status;
    private Long assignedLawyerId;
    private String assignedLawyerName;
    private Long createdCaseId;
    private List<LegalAidDocumentDto> documents;
    private List<LegalAidStatusHistoryDto> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
