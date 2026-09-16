package com.legaltrack.service;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.legalaid.*;
import com.legaltrack.enums.LegalAidStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface LegalAidService {
    LegalAidPreCheckResponse preCheckEligibility(LegalAidPreCheckRequest request);
    LegalAidApplicationDto applyForLegalAid(CreateLegalAidApplicationRequest request);
    LegalAidDocumentDto uploadApplicationDocument(Long applicationId, MultipartFile file, String documentName, String documentType);
    LegalAidApplicationDto getApplicationById(Long applicationId);
    PagedResponse<LegalAidApplicationDto> getMyApplications(Pageable pageable);
    PagedResponse<LegalAidApplicationDto> searchApplications(LegalAidStatus status, Long districtId, String query, Pageable pageable);
    LegalAidApplicationDto reviewApplication(Long applicationId, LegalAidVerificationRequest request);
    LegalAidApplicationDto assignLawyer(Long applicationId, AssignLawyerRequest request);
}
