package com.legaltrack.service;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.lawyer.*;
import org.springframework.web.multipart.MultipartFile;

public interface LawyerService {
    PagedResponse<LawyerProfileDto> searchLawyers(LawyerSearchCriteria criteria);
    LawyerProfileDto getLawyerById(Long lawyerId);
    LawyerProfileDto getLawyerByUserId(Long userId);
    LawyerProfileDto updateLawyerProfile(Long userId, LawyerProfileDto profileDto);
    LawyerAvailabilityDto updateAvailability(Long lawyerId, UpdateAvailabilityRequest request);
    LawyerVerificationDto submitVerificationDocument(Long lawyerId, MultipartFile document);
    LawyerVerificationDto reviewVerification(Long verificationId, boolean approved, String remarks);
}
