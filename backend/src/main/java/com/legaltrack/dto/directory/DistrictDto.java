package com.legaltrack.dto.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDto {
    private Long id;
    private Long stateId;
    private String stateName;
    private String name;
    private String code;
}
