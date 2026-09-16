package com.legaltrack.service.impl;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.lawyer.*;
import com.legaltrack.entity.*;
import com.legaltrack.enums.VerificationStatus;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.LawyerMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.LawyerService;
import com.legaltrack.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LawyerServiceImpl implements LawyerService {

    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerAvailabilityRepository availabilityRepository;
    private final LawyerVerificationRepository verificationRepository;
    private final LawyerSpecializationRepository specializationRepository;
    private final LawyerCourtRepository courtRepository;
    private final LawyerLanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtEntityRepository;
    private final LawyerMapper lawyerMapper;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<LawyerProfileDto> searchLawyers(LawyerSearchCriteria criteria) {
        Sort sort = Sort.by(
                criteria.getSortDirection().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                criteria.getSortBy()
        );
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        Page<LawyerProfile> page = lawyerProfileRepository.searchLawyers(
                criteria.getStateId(),
                criteria.getDistrictId(),
                criteria.getCourtId(),
                criteria.getSpecialization(),
                criteria.getLanguage(),
                criteria.getMinExperience(),
                criteria.getVerifiedOnly(),
                criteria.getAvailabilityStatus(),
                pageable
        );

        return PagedResponse.<LawyerProfileDto>builder()
                .content(page.getContent().stream().map(lawyerMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LawyerProfileDto getLawyerById(Long lawyerId) {
        LawyerProfile profile = lawyerProfileRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerProfile", "id", lawyerId));
        return lawyerMapper.toDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public LawyerProfileDto getLawyerByUserId(Long userId) {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerProfile", "userId", userId));
        return lawyerMapper.toDto(profile);
    }

    @Override
    @Transactional
    public LawyerProfileDto updateLawyerProfile(Long userId, LawyerProfileDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!userId.equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot update profile of another user");
        }

        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerProfile", "userId", userId));

        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getOfficeAddress() != null) profile.setOfficeAddress(dto.getOfficeAddress());
        if (dto.getExperienceYears() != null) profile.setExperienceYears(dto.getExperienceYears());

        if (dto.getSpecializations() != null) {
            specializationRepository.deleteByLawyerId(profile.getId());
            for (String spec : dto.getSpecializations()) {
                specializationRepository.save(LawyerSpecialization.builder()
                        .lawyer(profile)
                        .specialization(spec.trim())
                        .build());
            }
        }

        if (dto.getLanguages() != null) {
            languageRepository.deleteByLawyerId(profile.getId());
            for (String lang : dto.getLanguages()) {
                languageRepository.save(LawyerLanguage.builder()
                        .lawyer(profile)
                        .language(lang.trim())
                        .build());
            }
        }

        LawyerProfile updated = lawyerProfileRepository.save(profile);
        auditLogService.logAction(profile.getUser(), "LAWYER_PROFILE_UPDATE", "LawyerProfile", updated.getId(), null, "Updated lawyer profile");

        return lawyerMapper.toDto(updated);
    }

    @Override
    @Transactional
    public LawyerAvailabilityDto updateAvailability(Long lawyerId, UpdateAvailabilityRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LawyerProfile profile = lawyerProfileRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerProfile", "id", lawyerId));

        if (!profile.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot update availability of another lawyer");
        }

        LawyerAvailability availability = availabilityRepository.findByLawyerId(lawyerId)
                .orElseGet(() -> LawyerAvailability.builder().lawyer(profile).build());

        availability.setStatus(request.getStatus());
        if (request.getAvailableFrom() != null) availability.setAvailableFrom(request.getAvailableFrom());
        if (request.getAvailableUntil() != null) availability.setAvailableUntil(request.getAvailableUntil());

        LawyerAvailability saved = availabilityRepository.save(availability);
        return lawyerMapper.toAvailabilityDto(saved);
    }

    @Override
    @Transactional
    public LawyerVerificationDto submitVerificationDocument(Long lawyerId, MultipartFile document) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LawyerProfile profile = lawyerProfileRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerProfile", "id", lawyerId));

        if (!profile.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot submit verification for another lawyer");
        }

        String storageKey = fileStorageService.storeFile(document, "verifications");

        LawyerVerification verification = verificationRepository.findByLawyerId(lawyerId)
                .orElseGet(() -> LawyerVerification.builder().lawyer(profile).build());

        verification.setDocumentPath(storageKey);
        verification.setVerificationStatus(VerificationStatus.PENDING);
        verification.setRemarks("Document submitted for verification");

        LawyerVerification saved = verificationRepository.save(verification);
        return lawyerMapper.toVerificationDto(saved);
    }

    @Override
    @Transactional
    public LawyerVerificationDto reviewVerification(Long verificationId, boolean approved, String remarks) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User adminUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        LawyerVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerVerification", "id", verificationId));

        verification.setVerificationStatus(approved ? VerificationStatus.VERIFIED : VerificationStatus.REJECTED);
        verification.setVerifiedBy(adminUser);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setRemarks(remarks);

        LawyerProfile lawyer = verification.getLawyer();
        lawyer.setVerified(approved);
        lawyerProfileRepository.save(lawyer);

        LawyerVerification saved = verificationRepository.save(verification);
        auditLogService.logAction(adminUser, "LAWYER_VERIFICATION_REVIEW", "LawyerVerification", saved.getId(), null, "Verification status set to " + saved.getVerificationStatus());

        return lawyerMapper.toVerificationDto(saved);
    }
}
