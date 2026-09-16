package com.legaltrack.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLawyerRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;

    private String mobile;

    @NotBlank(message = "Bar registration number is required")
    private String barRegistrationNumber;

    @NotNull(message = "Enrollment year is required")
    private Integer enrollmentYear;

    private Integer experienceYears;

    @NotNull(message = "State is required")
    private Long stateId;

    @NotNull(message = "District is required")
    private Long districtId;

    private String officeAddress;
    private String bio;
    private List<String> specializations;
    private List<Long> courtIds;
    private List<String> languages;
}
