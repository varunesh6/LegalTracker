package com.legaltrack.dto.lawyer;

import com.legaltrack.enums.LawyerAvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawyerProfileDto {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String mobile;
    private String barRegistrationNumber;
    private Integer enrollmentYear;
    private Integer experienceYears;
    private Long stateId;
    private String stateName;
    private Long districtId;
    private String districtName;
    private String officeAddress;
    private String bio;
    private Boolean verified;
    private LawyerAvailabilityStatus availabilityStatus;
    private List<String> specializations;
    private List<CourtSummaryDto> courts;
    private List<String> languages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtSummaryDto {
        private Long courtId;
        private String courtName;
        private String courtComplexName;
    }
}
