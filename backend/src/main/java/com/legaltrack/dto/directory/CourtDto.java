package com.legaltrack.dto.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtDto {
    private Long id;
    private Long courtComplexId;
    private String courtComplexName;
    private String name;
    private String courtType;
    private String judgeDesignation;
}
