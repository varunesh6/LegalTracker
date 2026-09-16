package com.legaltrack.dto.casefile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseAdvocateDto {
    private Long id;
    private Long caseId;
    private String advocateName;
    private String registrationNumber;
    private String partyRepresented;
    private String role;
    private LocalDateTime createdAt;
}
