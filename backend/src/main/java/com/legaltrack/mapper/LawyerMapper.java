package com.legaltrack.mapper;

import com.legaltrack.dto.lawyer.LawyerAvailabilityDto;
import com.legaltrack.dto.lawyer.LawyerProfileDto;
import com.legaltrack.dto.lawyer.LawyerRequestDto;
import com.legaltrack.dto.lawyer.LawyerVerificationDto;
import com.legaltrack.entity.LawyerAvailability;
import com.legaltrack.entity.LawyerProfile;
import com.legaltrack.entity.LawyerRequest;
import com.legaltrack.entity.LawyerVerification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LawyerMapper {

    public LawyerProfileDto toDto(LawyerProfile profile) {
        if (profile == null) return null;

        return LawyerProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .name(profile.getUser().getName())
                .email(profile.getUser().getEmail())
                .mobile(profile.getUser().getMobile())
                .barRegistrationNumber(profile.getBarRegistrationNumber())
                .enrollmentYear(profile.getEnrollmentYear())
                .experienceYears(profile.getExperienceYears())
                .stateId(profile.getState() != null ? profile.getState().getId() : null)
                .stateName(profile.getState() != null ? profile.getState().getName() : null)
                .districtId(profile.getDistrict() != null ? profile.getDistrict().getId() : null)
                .districtName(profile.getDistrict() != null ? profile.getDistrict().getName() : null)
                .officeAddress(profile.getOfficeAddress())
                .bio(profile.getBio())
                .verified(profile.getVerified())
                .availabilityStatus(profile.getAvailability() != null ? profile.getAvailability().getStatus() : null)
                .specializations(profile.getSpecializations() != null ?
                        profile.getSpecializations().stream().map(s -> s.getSpecialization()).collect(Collectors.toList()) :
                        Collections.emptyList())
                .courts(profile.getCourts() != null ?
                        profile.getCourts().stream().map(c -> LawyerProfileDto.CourtSummaryDto.builder()
                                .courtId(c.getCourt().getId())
                                .courtName(c.getCourt().getName())
                                .courtComplexName(c.getCourt().getCourtComplex() != null ? c.getCourt().getCourtComplex().getName() : null)
                                .build()).collect(Collectors.toList()) :
                        Collections.emptyList())
                .languages(profile.getLanguages() != null ?
                        profile.getLanguages().stream().map(l -> l.getLanguage()).collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }

    public LawyerAvailabilityDto toAvailabilityDto(LawyerAvailability availability) {
        if (availability == null) return null;

        return LawyerAvailabilityDto.builder()
                .id(availability.getId())
                .lawyerId(availability.getLawyer().getId())
                .status(availability.getStatus())
                .availableFrom(availability.getAvailableFrom())
                .availableUntil(availability.getAvailableUntil())
                .updatedAt(availability.getUpdatedAt())
                .build();
    }

    public LawyerRequestDto toRequestDto(LawyerRequest request) {
        if (request == null) return null;

        return LawyerRequestDto.builder()
                .id(request.getId())
                .clientId(request.getClient().getId())
                .clientName(request.getClient().getName())
                .clientEmail(request.getClient().getEmail())
                .clientMobile(request.getClient().getMobile())
                .lawyerId(request.getLawyer().getId())
                .lawyerName(request.getLawyer().getName())
                .caseType(request.getCaseType())
                .courtId(request.getCourt() != null ? request.getCourt().getId() : null)
                .courtName(request.getCourt() != null ? request.getCourt().getName() : null)
                .message(request.getMessage())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    public LawyerVerificationDto toVerificationDto(LawyerVerification verification) {
        if (verification == null) return null;

        return LawyerVerificationDto.builder()
                .id(verification.getId())
                .lawyerId(verification.getLawyer().getId())
                .lawyerName(verification.getLawyer().getUser().getName())
                .barRegistrationNumber(verification.getLawyer().getBarRegistrationNumber())
                .documentPath(verification.getDocumentPath())
                .verificationStatus(verification.getVerificationStatus())
                .verifiedById(verification.getVerifiedBy() != null ? verification.getVerifiedBy().getId() : null)
                .verifiedByName(verification.getVerifiedBy() != null ? verification.getVerifiedBy().getName() : null)
                .verifiedAt(verification.getVerifiedAt())
                .remarks(verification.getRemarks())
                .build();
    }
}
