package com.legaltrack.dto.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseTypeDto {
    private Long id;
    private String name;
    private String code;
    private String category;
    private String description;
}
