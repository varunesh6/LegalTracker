package com.legaltrack.dto.support;

import com.legaltrack.enums.SupportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupportStatusRequest {

    @NotNull(message = "Status is required")
    private SupportStatus status;
}
