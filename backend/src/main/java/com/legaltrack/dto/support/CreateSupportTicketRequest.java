package com.legaltrack.dto.support;

import com.legaltrack.enums.SupportCategory;
import com.legaltrack.enums.SupportPriority;
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
public class CreateSupportTicketRequest {

    @NotNull(message = "Category is required")
    private SupportCategory category;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    @Builder.Default
    private SupportPriority priority = SupportPriority.MEDIUM;
}
