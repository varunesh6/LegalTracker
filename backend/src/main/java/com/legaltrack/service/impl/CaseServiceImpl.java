package com.legaltrack.service.impl;

import com.legaltrack.dto.casefile.*;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.document.CaseDocumentDto;
import com.legaltrack.entity.*;
import com.legaltrack.enums.*;
import com.legaltrack.exception.ApiException;
import com.legaltrack.exception.DuplicateResourceException;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.integration.court.CourtDataProvider;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.mapper.DocumentMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.CaseAttentionService;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.CaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseFileRepository caseFileRepository;
    private final CasePartyRepository partyRepository;
    private final CaseAdvocateRepository advocateRepository;
    private final CaseEventRepository eventRepository;
    private final CaseNoteRepository noteRepository;
    private final CaseDocumentRepository documentRepository;
    private final TrackedCaseRepository trackedCaseRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final CourtComplexRepository courtComplexRepository;
    private final PoliceStationRepository policeStationRepository;
    private final CourtDataProvider mockCourtDataProvider;
    private final CaseMapper caseMapper;
    private final DocumentMapper documentMapper;
    private final CaseDiaryService caseDiaryService;
    private final CaseAttentionService caseAttentionService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CaseFileDto createCase(CreateCaseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        // Generate internal reference
        String internalRef = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        User client = request.getClientId() != null ?
                userRepository.findById(request.getClientId()).orElse(currentUser) :
                currentUser;

        User lawyer = request.getLawyerId() != null ?
                userRepository.findById(request.getLawyerId()).orElse(null) :
                (SecurityUtils.isLawyer() ? currentUser : null);

        Court court = request.getCourtId() != null ? courtRepository.findById(request.getCourtId()).orElse(null) : null;
        State state = request.getStateId() != null ? stateRepository.findById(request.getStateId()).orElse(null) : null;
        District district = request.getDistrictId() != null ? districtRepository.findById(request.getDistrictId()).orElse(null) : null;
        CourtComplex cc = request.getCourtComplexId() != null ? courtComplexRepository.findById(request.getCourtComplexId()).orElse(null) : null;
        PoliceStation ps = request.getPoliceStationId() != null ? policeStationRepository.findById(request.getPoliceStationId()).orElse(null) : null;

        CaseFile caseFile = CaseFile.builder()
                .internalReferenceId(internalRef)
                .client(client)
                .lawyer(lawyer)
                .court(court)
                .state(state)
                .district(district)
                .courtComplex(cc)
                .caseType(request.getCaseType())
                .caseCategory(request.getCaseCategory() != null ? request.getCaseCategory() : "CIVIL")
                .title(request.getTitle())
                .cnrNumber(request.getCnrNumber())
                .caseNumber(request.getCaseNumber())
                .filingNumber(request.getFilingNumber())
                .filingDate(request.getFilingDate())
                .registrationNumber(request.getRegistrationNumber())
                .registrationDate(request.getRegistrationDate())
                .firNumber(request.getFirNumber())
                .firYear(request.getFirYear())
                .policeStation(ps)
                .act(request.getAct())
                .section(request.getSection())
                .status(request.getStatus() != null ? request.getStatus() : CaseStatus.PENDING)
                .stage(request.getStage() != null ? request.getStage() : CaseStage.APPEARANCE)
                .matterDescription(request.getMatterDescription())
                .engagementType(request.getEngagementType() != null ? request.getEngagementType() : EngagementType.PRIVATE_LAWYER)
                .isDemoData(true)
                .nextHearingDate(request.getNextHearingDate())
                .build();

        CaseFile saved = caseFileRepository.save(caseFile);

        // Save parties
        if (request.getParties() != null) {
            for (CasePartyDto pDto : request.getParties()) {
                partyRepository.save(CaseParty.builder()
                        .caseFile(saved)
                        .name(pDto.getName())
                        .partyType(pDto.getPartyType() != null ? pDto.getPartyType() : PartyType.PLAINTIFF)
                        .isPrimary(pDto.getIsPrimary() != null ? pDto.getIsPrimary() : false)
                        .contactInfo(pDto.getContactInfo())
                        .build());
            }
        }

        // Save advocates
        if (request.getAdvocates() != null) {
            for (CaseAdvocateDto aDto : request.getAdvocates()) {
                advocateRepository.save(CaseAdvocate.builder()
                        .caseFile(saved)
                        .advocateName(aDto.getAdvocateName())
                        .registrationNumber(aDto.getRegistrationNumber())
                        .partyRepresented(aDto.getPartyRepresented())
                        .role(aDto.getRole())
                        .build());
            }
        }

        // Record initial case event & diary
        eventRepository.save(CaseEvent.builder()
                .caseFile(saved)
                .eventType("CASE_CREATED")
                .eventTitle("Case Workspace Initialized")
                .eventDescription("Case workspace initialized for " + saved.getTitle())
                .eventDate(LocalDateTime.now())
                .source(EventSource.SYSTEM)
                .createdBy(currentUser)
                .build());

        caseDiaryService.recordAutomaticDiaryEntry(
                saved,
                currentUser,
                DiaryEntryType.SYSTEM_UPDATE,
                "Case Workspace Created",
                "Case reference: " + internalRef,
                LocalDateTime.now(),
                DiaryVisibility.SHARED
        );

        // Auto-track case for creator
        trackedCaseRepository.save(TrackedCase.builder()
                .user(currentUser)
                .caseFile(saved)
                .nickname(saved.getTitle())
                .notificationsEnabled(true)
                .trackedAt(LocalDateTime.now())
                .build());

        // Evaluate initial attention
        caseAttentionService.evaluateAttentionRulesForCase(saved);

        auditLogService.logAction(currentUser, "CASE_CREATE", "CaseFile", saved.getId(), null, "Created case " + saved.getTitle());

        return caseMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CaseFileDto updateCase(Long caseId, UpdateCaseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        validateCaseAccess(caseFile, currentUserId);

        if (request.getTitle() != null) caseFile.setTitle(request.getTitle());
        if (request.getStatus() != null) caseFile.setStatus(request.getStatus());
        if (request.getStage() != null) caseFile.setStage(request.getStage());
        if (request.getMatterDescription() != null) caseFile.setMatterDescription(request.getMatterDescription());
        if (request.getNextHearingDate() != null) caseFile.setNextHearingDate(request.getNextHearingDate());
        if (request.getAct() != null) caseFile.setAct(request.getAct());
        if (request.getSection() != null) caseFile.setSection(request.getSection());

        CaseFile updated = caseFileRepository.save(caseFile);

        caseDiaryService.recordAutomaticDiaryEntry(
                updated,
                currentUser,
                DiaryEntryType.SYSTEM_UPDATE,
                "Case Information Updated",
                "Status: " + updated.getStatus() + ", Stage: " + updated.getStage(),
                LocalDateTime.now(),
                DiaryVisibility.SHARED
        );

        caseAttentionService.evaluateAttentionRulesForCase(updated);
        auditLogService.logAction(currentUser, "CASE_UPDATE", "CaseFile", updated.getId(), null, "Updated case " + updated.getTitle());

        return caseMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseFileDto getCaseById(Long caseId) {
        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        validateCaseAccess(caseFile, currentUserId);

        return caseMapper.toDto(caseFile);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseDetailsDto getCaseDetails(Long caseId) {
        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        validateCaseAccess(caseFile, currentUserId);

        Optional<TrackedCase> tracked = currentUserId != null ?
                trackedCaseRepository.findByUserIdAndCaseFileId(currentUserId, caseId) :
                Optional.empty();

        List<CasePartyDto> parties = partyRepository.findByCaseFileId(caseId).stream()
                .map(caseMapper::toPartyDto).toList();

        List<CaseAdvocateDto> advocates = advocateRepository.findByCaseFileId(caseId).stream()
                .map(caseMapper::toAdvocateDto).toList();

        List<CaseHearingDto> hearings = caseFile.getHearings().stream()
                .map(caseMapper::toHearingDto).toList();

        List<CaseOrderDto> orders = caseFile.getOrders().stream()
                .map(caseMapper::toOrderDto).toList();

        List<CaseDocumentDto> documents = documentRepository.findAuthorizedDocuments(caseId, currentUserId != null ? currentUserId : -1L, SecurityUtils.isAdmin()).stream()
                .map(documentMapper::toDto).toList();

        List<CaseDiaryEntryDto> diaryEntries = caseDiaryService.getDiaryEntriesForCase(caseId);
        List<CaseAttentionDto> attentionItems = caseAttentionService.getAttentionForCase(caseId);
        List<CaseNoteDto> notes = getCaseNotes(caseId);

        return CaseDetailsDto.builder()
                .caseFile(caseMapper.toDto(caseFile))
                .parties(parties)
                .advocates(advocates)
                .hearings(hearings)
                .orders(orders)
                .documents(documents)
                .diaryEntries(diaryEntries)
                .attentionItems(attentionItems)
                .notes(notes)
                .isTrackedByCurrentUser(tracked.isPresent())
                .isNotificationsEnabled(tracked.map(TrackedCase::getNotificationsEnabled).orElse(false))
                .userNickname(tracked.map(TrackedCase::getNickname).orElse(null))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CaseTimelineResponse getCaseTimeline(Long caseId) {
        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        List<CaseEvent> events = eventRepository.findByCaseFileIdOrderByEventDateAsc(caseId);
        return caseMapper.toTimelineResponse(caseFile, events);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CaseFileDto> searchCases(CaseSearchCriteria criteria) {
        Sort sort = Sort.by(
                criteria.getSortDirection().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                criteria.getSortBy()
        );
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        Page<CaseFile> page = caseFileRepository.searchCases(
                criteria.getClientId(),
                criteria.getLawyerId(),
                criteria.getStatus(),
                criteria.getQuery(),
                pageable
        );

        return PagedResponse.<CaseFileDto>builder()
                .content(page.getContent().stream().map(caseMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CaseFileDto> getMyCases(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) throw new ApiException("User not authenticated");

        Page<CaseFile> page;
        if (SecurityUtils.isLawyer()) {
            page = caseFileRepository.findByLawyerId(currentUserId, pageable);
        } else if (SecurityUtils.isAdmin()) {
            page = caseFileRepository.findAll(pageable);
        } else {
            page = caseFileRepository.findByClientId(currentUserId, pageable);
        }

        return PagedResponse.<CaseFileDto>builder()
                .content(page.getContent().stream().map(caseMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public CaseNoteDto addCaseNote(Long caseId, CreateNoteRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        validateCaseAccess(caseFile, currentUserId);

        CaseNote note = CaseNote.builder()
                .caseFile(caseFile)
                .user(currentUser)
                .title(request.getTitle())
                .content(request.getContent())
                .visibility(request.getVisibility() != null ? request.getVisibility() : DiaryVisibility.CLIENT_PRIVATE)
                .build();

        CaseNote saved = noteRepository.save(note);
        return caseMapper.toNoteDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseNoteDto> getCaseNotes(Long caseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (SecurityUtils.isAdmin()) {
            return noteRepository.findByCaseFileIdAndUserId(caseId, currentUserId).stream()
                    .map(caseMapper::toNoteDto).toList();
        }

        return noteRepository.findAuthorizedNotes(caseId, currentUserId != null ? currentUserId : -1L).stream()
                .map(caseMapper::toNoteDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CaseDetailsDto trackByCnr(TrackByCnrRequest request) {
        Optional<CaseFileDto> found = mockCourtDataProvider.searchByCnr(request.getCnrNumber().trim());
        if (found.isEmpty()) {
            throw new ResourceNotFoundException("Case with CNR " + request.getCnrNumber() + " not found in court data repository");
        }

        // If it exists in DB, fetch full details
        Optional<CaseFile> dbCase = caseFileRepository.findByCnrNumber(request.getCnrNumber().trim());
        if (dbCase.isPresent()) {
            return getCaseDetails(dbCase.get().getId());
        }

        // Return standalone mock case response
        return CaseDetailsDto.builder()
                .caseFile(found.get())
                .parties(List.of())
                .advocates(List.of())
                .hearings(List.of())
                .orders(List.of())
                .documents(List.of())
                .diaryEntries(List.of())
                .attentionItems(List.of())
                .notes(List.of())
                .isTrackedByCurrentUser(false)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CaseDetailsDto trackByCaseNumber(TrackByCaseNumberRequest request) {
        Optional<CaseFileDto> found = mockCourtDataProvider.searchByCaseNumber(request.getCaseNumber().trim(), request.getCourtId(), request.getFilingYear());
        if (found.isEmpty()) {
            throw new ResourceNotFoundException("Case with Number " + request.getCaseNumber() + " not found in court data repository");
        }

        Optional<CaseFile> dbCase = caseFileRepository.findByCaseNumber(request.getCaseNumber().trim());
        if (dbCase.isPresent()) {
            return getCaseDetails(dbCase.get().getId());
        }

        return CaseDetailsDto.builder()
                .caseFile(found.get())
                .parties(List.of())
                .advocates(List.of())
                .hearings(List.of())
                .orders(List.of())
                .documents(List.of())
                .diaryEntries(List.of())
                .attentionItems(List.of())
                .notes(List.of())
                .isTrackedByCurrentUser(false)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CaseDetailsDto trackByFir(TrackByFirRequest request) {
        Optional<CaseFileDto> found = mockCourtDataProvider.searchByFirNumber(request.getFirNumber().trim(), request.getFirYear(), request.getPoliceStationId());
        if (found.isEmpty()) {
            throw new ResourceNotFoundException("Case with FIR " + request.getFirNumber() + " not found in court data repository");
        }

        Optional<CaseFile> dbCase = caseFileRepository.findByFirNumber(request.getFirNumber().trim());
        if (dbCase.isPresent()) {
            return getCaseDetails(dbCase.get().getId());
        }

        return CaseDetailsDto.builder()
                .caseFile(found.get())
                .parties(List.of())
                .advocates(List.of())
                .hearings(List.of())
                .orders(List.of())
                .documents(List.of())
                .diaryEntries(List.of())
                .attentionItems(List.of())
                .notes(List.of())
                .isTrackedByCurrentUser(false)
                .build();
    }

    private void validateCaseAccess(CaseFile caseFile, Long userId) {
        if (userId == null) return;
        if (SecurityUtils.isAdmin()) return;

        boolean isClient = caseFile.getClient() != null && caseFile.getClient().getId().equals(userId);
        boolean isLawyer = caseFile.getLawyer() != null && caseFile.getLawyer().getId().equals(userId);
        boolean isTracked = trackedCaseRepository.existsByUserIdAndCaseFileId(userId, caseFile.getId());

        if (!isClient && !isLawyer && !isTracked) {
            throw new UnauthorizedAccessException("You are not authorized to access this case file");
        }
    }
}
