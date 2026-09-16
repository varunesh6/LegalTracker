package com.legaltrack.dto.casefile;

import com.legaltrack.enums.PartyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasePartyDto {
    private Long id;
    private Long caseId;
    private String name;
    private PartyType partyType;
    private Boolean isPrimary;
    private String contactInfo;
    private LocalDateTime createdAt;
}
