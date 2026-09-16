package com.legaltrack.dto.casefile;

import com.legaltrack.enums.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseSearchCriteria {
    private String query;
    private Long clientId;
    private Long lawyerId;
    private CaseStatus status;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDirection = "desc";
}
