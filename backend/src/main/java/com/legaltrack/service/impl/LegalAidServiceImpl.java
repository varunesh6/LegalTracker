package com.legaltrack.service.impl;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.legalaid.*;
import com.legaltrack.entity.*;
import com.legaltrack.enums.*;
import com.legaltrack.exception.ApiException;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.LegalAidMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.LegalAidService;
import com.legaltrack.service.NotificationService;
import com.legaltrack.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegalAidServiceImpl implements LegalAidService {

    private final LegalAidApplicationRepository applicationRepository;
    private final LegalAidDocumentRepository documentRepository;
    private final LegalAidStatusHistoryRepository historyRepository;
    private final LegalAidAssignmentRepository assignmentRepository;
    private final LegalAidEligibilityRuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final CourtRepository courtRepository;
    private final CaseFileRepository caseFileRepository;
    private final TrackedCaseRepository trackedCaseRepository;
    private final LegalAidMapper legalAidMapper;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    public LegalAidPreCheckResponse preCheckEligibility(LegalAidPreCheckRequest request) {
        BigDecimal defaultLimit = new BigDecimal("300000.00");
        BigDecimal applicantIncome = request.getAnnualIncome();

        boolean isCategoricallyExempt = request.getCategory() == LegalAidCategory.WOMAN_OR_CHILD
                || request.getCategory() == LegalAidCategory.SCHEDULED_CASTE_OR_TRIBE
                || request.getCategory() == LegalAidCategory.PERSON_WITH_DISABILITY
                || request.getCategory() == LegalAidCategory.PERSON_IN_CUSTODY
                || request.getCategory() == LegalAidCategory.VICTIM_OF_TRAFFICKING_OR_BEGAR;

        boolean isIncomeEligible = applicantIncome.compareTo(defaultLimit) <= 0;

        List<String> requiredDocs = new ArrayList<>();
        requiredDocs.add("Identity Proof (Aadhaar / Voter ID / Passport)");
        requiredDocs.add("Address Proof (Utility Bill / Ration Card)");

        if (request.getCategory() == LegalAidCategory.LOW_INCOME_GENERAL || !isCategoricallyExempt) {
            requiredDocs.add("Income Certificate issued by Revenue Authority (Tahsildar)");
        } else {
            requiredDocs.add("Category Supporting Proof (Community Certificate / Disability Card / Custody Order)");
        }
        requiredDocs.add("Relevant Case Documents & Plaint/Notice if already issued");

        if (isCategoricallyExempt || isIncomeEligible) {
            return LegalAidPreCheckResponse.builder()
                    .eligible(true)
                    .status("POTENTIALLY_ELIGIBLE")
                    .categoryEvaluated(request.getCategory().name())
                    .incomeLimit(defaultLimit)
                    .applicantIncome(applicantIncome)
                    .message("You are potentially eligible for free legal aid assistance based on the details submitted.")
                    .disclaimer("Final eligibility is subject to verification of submitted certificates and documents by the competent Legal Services Authority.")
                    .requiredDocuments(requiredDocs)
                    .build();
        } else {
            return LegalAidPreCheckResponse.builder()
                    .eligible(false)
                    .status("REQUIRES_MANUAL_VERIFICATION")
                    .categoryEvaluated(request.getCategory().name())
                    .incomeLimit(defaultLimit)
                    .applicantIncome(applicantIncome)
                    .message("Annual income exceeds standard statutory threshold, but you may apply for special consideration before the Authority.")
                    .disclaimer("Discretionary legal aid may be considered in exceptional circumstances by the Member Secretary.")
                    .requiredDocuments(requiredDocs)
                    .build();
        }
    }

    @Override
    @Transactional
    public LegalAidApplicationDto applyForLegalAid(CreateLegalAidApplicationRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User client = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        State state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State", "id", request.getStateId()));

        District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", request.getDistrictId()));

        Court court = request.getCourtId() != null ? courtRepository.findById(request.getCourtId()).orElse(null) : null;

        String appNumber = "LA-" + state.getCode() + "-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LegalAidApplication app = LegalAidApplication.builder()
                .applicationNumber(appNumber)
                .client(client)
                .court(court)
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail() != null ? request.getEmail() : client.getEmail())
                .address(request.getAddress())
                .state(state)
                .district(district)
                .caseType(request.getCaseType())
                .caseStage(request.getCaseStage())
                .matterDescription(request.getMatterDescription())
                .opponentInformation(request.getOpponentInformation())
                .annualIncome(request.getAnnualIncome())
                .employmentStatus(request.getEmploymentStatus())
                .selectedCategory(request.getSelectedCategory())
                .supportingInformation(request.getSupportingInformation())
                .status(LegalAidStatus.SUBMITTED)
                .build();

        LegalAidApplication saved = applicationRepository.save(app);

        // Record status history
        historyRepository.save(LegalAidStatusHistory.builder()
                .application(saved)
                .previousStatus(null)
                .newStatus(LegalAidStatus.SUBMITTED)
                .remarks("Application submitted by applicant")
                .changedBy(client)
                .build());

        // Notify client
        notificationService.createNotification(
                client,
                NotificationType.LEGAL_AID_UPDATE,
                "Legal Aid Application Submitted",
                "Your application " + appNumber + " has been received and is awaiting document review.",
                "LEGAL_AID",
                saved.getId(),
                "/client/legal-aid/" + saved.getId()
        );

        auditLogService.logAction(client, "LEGAL_AID_APPLY", "LegalAidApplication", saved.getId(), null, "Submitted application " + appNumber);

        return legalAidMapper.toDto(saved);
    }

    @Override
    @Transactional
    public LegalAidDocumentDto uploadApplicationDocument(Long applicationId, MultipartFile file, String documentName, String documentType) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LegalAidApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("LegalAidApplication", "id", applicationId));

        if (!app.getClient().getId().equals(currentUserId) && !SecurityUtils.isAdmin() && !SecurityUtils.isLegalAidOfficer()) {
            throw new UnauthorizedAccessException("Cannot upload document for another user's application");
        }

        String storageKey = fileStorageService.storeFile(file, "legal-aid");

        LegalAidDocument doc = LegalAidDocument.builder()
                .application(app)
                .documentName(documentName != null ? documentName : file.getOriginalFilename())
                .documentType(documentType != null ? documentType : "OTHER")
                .storageKey(storageKey)
                .mimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .fileSize(file.getSize())
                .verificationStatus(VerificationStatus.PENDING)
                .remarks("Uploaded by applicant")
                .build();

        LegalAidDocument saved = documentRepository.save(doc);
        return legalAidMapper.toDocumentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LegalAidApplicationDto getApplicationById(Long applicationId) {
        LegalAidApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("LegalAidApplication", "id", applicationId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!app.getClient().getId().equals(currentUserId) && !SecurityUtils.isAdmin() && !SecurityUtils.isLegalAidOfficer()) {
            throw new UnauthorizedAccessException("Cannot view application of another user");
        }

        return legalAidMapper.toDto(app);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<LegalAidApplicationDto> getMyApplications(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Page<LegalAidApplication> page = applicationRepository.findByClientId(currentUserId, pageable);

        return PagedResponse.<LegalAidApplicationDto>builder()
                .content(page.getContent().stream().map(legalAidMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<LegalAidApplicationDto> searchApplications(LegalAidStatus status, Long districtId, String query, Pageable pageable) {
        Page<LegalAidApplication> page = applicationRepository.searchApplications(status, districtId, query, pageable);

        return PagedResponse.<LegalAidApplicationDto>builder()
                .content(page.getContent().stream().map(legalAidMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public LegalAidApplicationDto reviewApplication(Long applicationId, LegalAidVerificationRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User reviewer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        LegalAidApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("LegalAidApplication", "id", applicationId));

        LegalAidStatus prevStatus = app.getStatus();
        app.setStatus(request.getNewStatus());

        // Update document verification statuses if provided
        if (request.getDocumentVerifications() != null) {
            for (var entry : request.getDocumentVerifications().entrySet()) {
                documentRepository.findById(entry.getKey()).ifPresent(doc -> {
                    doc.setVerificationStatus(entry.getValue());
                    documentRepository.save(doc);
                });
            }
        }

        LegalAidApplication updated = applicationRepository.save(app);

        historyRepository.save(LegalAidStatusHistory.builder()
                .application(updated)
                .previousStatus(prevStatus)
                .newStatus(request.getNewStatus())
                .remarks(request.getRemarks())
                .changedBy(reviewer)
                .build());

        // Notify client
        notificationService.createNotification(
                app.getClient(),
                NotificationType.LEGAL_AID_UPDATE,
                "Legal Aid Status Update: " + request.getNewStatus(),
                "Your application " + app.getApplicationNumber() + " status is now " + request.getNewStatus() + (request.getRemarks() != null ? " (" + request.getRemarks() + ")" : ""),
                "LEGAL_AID",
                updated.getId(),
                "/client/legal-aid/" + updated.getId()
        );

        auditLogService.logAction(reviewer, "LEGAL_AID_REVIEW", "LegalAidApplication", updated.getId(), prevStatus.name(), request.getNewStatus().name());

        return legalAidMapper.toDto(updated);
    }

    @Override
    @Transactional
    public LegalAidApplicationDto assignLawyer(Long applicationId, AssignLawyerRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User assigner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        LegalAidApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("LegalAidApplication", "id", applicationId));

        User lawyer = userRepository.findById(request.getLawyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer User", "id", request.getLawyerId()));

        app.setAssignedLawyer(lawyer);
        app.setStatus(LegalAidStatus.LAWYER_ASSIGNED);

        // Record assignment
        assignmentRepository.save(LegalAidAssignment.builder()
                .application(app)
                .lawyer(lawyer)
                .assignedBy(assigner)
                .instructions(request.getInstructions())
                .build());

        // Automatically create CaseFile for this legal-aid grant
        String internalRef = "REF-LA-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        CaseFile legalAidCase = CaseFile.builder()
                .internalReferenceId(internalRef)
                .client(app.getClient())
                .lawyer(lawyer)
                .court(app.getCourt())
                .state(app.getState())
                .district(app.getDistrict())
                .caseType(app.getCaseType())
                .caseCategory("LEGAL_AID")
                .title("LEGAL AID: " + app.getFullName() + " (" + app.getCaseType() + ")")
                .matterDescription(app.getMatterDescription())
                .engagementType(EngagementType.LEGAL_AID)
                .status(CaseStatus.PENDING)
                .stage(CaseStage.FILING)
                .isDemoData(true)
                .build();

        CaseFile savedCase = caseFileRepository.save(legalAidCase);
        app.setCreatedCase(savedCase);
        app.setStatus(LegalAidStatus.CASE_CREATED);

        LegalAidApplication updated = applicationRepository.save(app);

        // Auto-track case for client
        trackedCaseRepository.save(TrackedCase.builder()
                .user(app.getClient())
                .caseFile(savedCase)
                .nickname("Legal Aid Case: " + app.getApplicationNumber())
                .notificationsEnabled(true)
                .build());

        // Notify client & lawyer
        notificationService.createNotification(
                app.getClient(),
                NotificationType.LEGAL_AID_UPDATE,
                "Legal Aid Lawyer Assigned",
                "Advocate " + lawyer.getName() + " has been assigned to represent your legal aid matter.",
                "LEGAL_AID",
                updated.getId(),
                "/client/cases/" + savedCase.getId()
        );

        notificationService.createNotification(
                lawyer,
                NotificationType.LEGAL_AID_UPDATE,
                "New Legal Aid Case Assigned",
                "You have been assigned to represent " + app.getFullName() + " under Legal Aid grant " + app.getApplicationNumber(),
                "LEGAL_AID",
                updated.getId(),
                "/lawyer/cases/" + savedCase.getId()
        );

        auditLogService.logAction(assigner, "LEGAL_AID_ASSIGN_LAWYER", "LegalAidApplication", updated.getId(), null, "Assigned lawyer " + lawyer.getName() + " to application " + app.getApplicationNumber());

        return legalAidMapper.toDto(updated);
    }
}
