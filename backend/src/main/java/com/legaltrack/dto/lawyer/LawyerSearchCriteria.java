package com.legaltrack.dto.lawyer;

import com.legaltrack.enums.LawyerAvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawyerSearchCriteria {
    private Long stateId;
    private Long districtId;
    private Long courtId;
    private String specialization;
    private String language;
    private Integer minExperience;
    private Boolean verifiedOnly;
    private LawyerAvailabilityStatus availabilityStatus;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;
    @Builder.Default
    private String sortBy = "experienceYears";
    @Builder.Default
    private String sortDirection = "desc";
}
