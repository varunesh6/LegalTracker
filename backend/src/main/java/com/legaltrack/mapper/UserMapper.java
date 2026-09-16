package com.legaltrack.mapper;

import com.legaltrack.dto.auth.UserProfileDto;
import com.legaltrack.entity.ClientProfile;
import com.legaltrack.entity.LawyerProfile;
import com.legaltrack.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserProfileDto toProfileDto(User user, ClientProfile clientProfile, LawyerProfile lawyerProfile) {
        UserProfileDto.UserProfileDtoBuilder builder = UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt());

        if (clientProfile != null) {
            builder.address(clientProfile.getAddress())
                    .stateId(clientProfile.getState() != null ? clientProfile.getState().getId() : null)
                    .stateName(clientProfile.getState() != null ? clientProfile.getState().getName() : null)
                    .districtId(clientProfile.getDistrict() != null ? clientProfile.getDistrict().getId() : null)
                    .districtName(clientProfile.getDistrict() != null ? clientProfile.getDistrict().getName() : null)
                    .pincode(clientProfile.getPincode())
                    .occupation(clientProfile.getOccupation());
        }

        if (lawyerProfile != null) {
            builder.lawyerProfileId(lawyerProfile.getId())
                    .barRegistrationNumber(lawyerProfile.getBarRegistrationNumber())
                    .enrollmentYear(lawyerProfile.getEnrollmentYear())
                    .experienceYears(lawyerProfile.getExperienceYears())
                    .stateId(lawyerProfile.getState() != null ? lawyerProfile.getState().getId() : null)
                    .stateName(lawyerProfile.getState() != null ? lawyerProfile.getState().getName() : null)
                    .districtId(lawyerProfile.getDistrict() != null ? lawyerProfile.getDistrict().getId() : null)
                    .districtName(lawyerProfile.getDistrict() != null ? lawyerProfile.getDistrict().getName() : null)
                    .officeAddress(lawyerProfile.getOfficeAddress())
                    .bio(lawyerProfile.getBio())
                    .verified(lawyerProfile.getVerified())
                    .availabilityStatus(lawyerProfile.getAvailability() != null ? lawyerProfile.getAvailability().getStatus().name() : null)
                    .specializations(lawyerProfile.getSpecializations().stream().map(s -> s.getSpecialization()).collect(Collectors.toList()))
                    .courtNames(lawyerProfile.getCourts().stream().map(c -> c.getCourt().getName()).collect(Collectors.toList()))
                    .languages(lawyerProfile.getLanguages().stream().map(l -> l.getLanguage()).collect(Collectors.toList()));
        }

        return builder.build();
    }
}
