package com.legaltrack.dto.casefile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackByCnrRequest {

    @NotBlank(message = "CNR number is required")
    private String cnrNumber;
}
