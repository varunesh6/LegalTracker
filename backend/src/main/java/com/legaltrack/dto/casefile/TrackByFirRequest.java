package com.legaltrack.dto.casefile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackByFirRequest {

    @NotBlank(message = "FIR number is required")
    private String firNumber;

    @NotNull(message = "FIR year is required")
    private Integer firYear;

    private Long policeStationId;
}
