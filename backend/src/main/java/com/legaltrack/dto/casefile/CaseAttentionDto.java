package com.legaltrack.dto.casefile;

import com.legaltrack.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseAttentionDto {
    private Long id;
    private Long caseId;
    private String caseTitle;
    private String caseNumber;
    private String cnrNumber;
    private String type;
    private String title;
    private String description;
    private Severity severity;
    private String actionUrl;
    private Boolean resolved;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}
