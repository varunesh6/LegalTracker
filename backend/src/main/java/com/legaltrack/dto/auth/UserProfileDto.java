package com.legaltrack.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String status;
    private List<String> roles;
    private LocalDateTime createdAt;

    // Client specific fields
    private String address;
    private String stateName;
    private Long stateId;
    private String districtName;
    private Long districtId;
    private String pincode;
    private String occupation;

    // Lawyer specific fields
    private Long lawyerProfileId;
    private String barRegistrationNumber;
    private Integer enrollmentYear;
    private Integer experienceYears;
    private String officeAddress;
    private String bio;
    private Boolean verified;
    private String availabilityStatus;
    private List<String> specializations;
    private List<String> courtNames;
    private List<String> languages;
}
