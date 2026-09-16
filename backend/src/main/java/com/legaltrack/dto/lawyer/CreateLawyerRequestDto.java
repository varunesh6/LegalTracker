package com.legaltrack.dto.lawyer;

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
public class CreateLawyerRequestDto {

    @NotNull(message = "Lawyer ID is required")
    private Long lawyerId;

    @NotBlank(message = "Case type is required")
    private String caseType;

    private Long courtId;

    @NotBlank(message = "Message describing legal requirement is required")
    private String message;
}
