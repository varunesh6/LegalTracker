package com.legaltrack.dto.directory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtComplexDto {
    private Long id;
    private Long districtId;
    private String districtName;
    private String name;
    private String address;
    private List<CourtDto> courts;
}
