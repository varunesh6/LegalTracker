package com.legaltrack.dto.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoliceStationDto {
    private Long id;
    private Long districtId;
    private String districtName;
    private String name;
    private String code;
}
