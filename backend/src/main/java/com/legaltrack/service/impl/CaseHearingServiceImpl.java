package com.legaltrack.service.impl;

import com.legaltrack.dto.casefile.CaseHearingDto;
import com.legaltrack.dto.casefile.CreateHearingRequest;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.CaseHearing;
import com.legaltrack.entity.Court;
import com.legaltrack.entity.User;
import com.legaltrack.enums.*;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.CaseHearingRepository;
import com.legaltrack.repository.CourtRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.CaseAttentionService;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.CaseHearingService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseHearingServiceImpl implements CaseHearingService {

    private final CaseHearingRepository hearingRepository;
    private final CaseFileRepository caseFileRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final CaseDiaryService caseDiaryService;
    private final CaseAttentionService caseAttentionService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CaseHearingDto scheduleHearing(Long caseId, CreateHearingRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        validateCaseAccess(caseFile, currentUserId);

        Court court = request.getCourtId() != null ?
                courtRepository.findById(request.getCourtId()).orElse(caseFile.getCourt()) :
                caseFile.getCourt();

        CaseHearing hearing = CaseHearing.builder()
                .caseFile(caseFile)
                .hearingDate(request.getHearingDate())
                .hearingTime(request.getHearingTime())
                .court(court)
                .judgeName(request.getJudgeName())
                .purpose(request.getPurpose())
                .stage(request.getStage() != null ? request.getStage() : (caseFile.getStage() != null ? caseFile.getStage().name() : "Appearance"))
                .status(request.getStatus() != null ? request.getStatus() : HearingStatus.SCHEDULED)
                .notes(request.getNotes())
                .nextHearingDate(request.getNextHearingDate())
                .source(SecurityUtils.isAdmin() ? "ADMIN" : "USER")
                .build();

        CaseHearing saved = hearingRepository.save(hearing);

        // Update case file next hearing date
        caseFile.setNextHearingDate(request.getHearingDate());
        caseFileRepository.save(caseFile);

        // Record automated diary entry
        caseDiaryService.recordAutomaticDiaryEntry(
                caseFile,
                currentUser,
                DiaryEntryType.HEARING,
                "Hearing Scheduled: " + request.getHearingDate(),
                (request.getPurpose() != null ? request.getPurpose() : "Hearing scheduled") + (request.getHearingTime() != null ? " at " + request.getHearingTime() : ""),
                LocalDateTime.now(),
                DiaryVisibility.SHARED
        );

        // Evaluate Attention
        caseAttentionService.evaluateAttentionRulesForCase(caseFile);

        // Notify client & lawyer
        String notifMsg = "Hearing for " + caseFile.getTitle() + " is scheduled on " + request.getHearingDate();
        if (caseFile.getClient() != null) {
            notificationService.createNotification(caseFile.getClient(), NotificationType.HEARING_REMINDER, "Hearing Scheduled", notifMsg, "CASE", caseFile.getId(), "/client/cases/" + caseFile.getId() + "/hearings");
        }
        if (caseFile.getLawyer() != null) {
            notificationService.createNotification(caseFile.getLawyer(), NotificationType.HEARING_REMINDER, "Hearing Scheduled", notifMsg, "CASE", caseFile.getId(), "/lawyer/cases/" + caseFile.getId() + "/hearings");
        }

        return caseMapper.toHearingDto(saved);
    }

    @Override
    @Transactional
    public CaseHearingDto updateHearingStatus(Long hearingId, HearingStatus status, String notes, LocalDate nextHearingDate) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseHearing hearing = hearingRepository.findById(hearingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing", "id", hearingId));

        CaseFile caseFile = hearing.getCaseFile();
        validateCaseAccess(caseFile, currentUserId);

        hearing.setStatus(status);
        if (notes != null) hearing.setNotes(notes);
        if (nextHearingDate != null) {
            hearing.setNextHearingDate(nextHearingDate);
            caseFile.setNextHearingDate(nextHearingDate);
            caseFileRepository.save(caseFile);
        }

        CaseHearing updated = hearingRepository.save(hearing);

        caseDiaryService.recordAutomaticDiaryEntry(
                caseFile,
                currentUser,
                DiaryEntryType.HEARING,
                "Hearing " + status.name() + (nextHearingDate != null ? " - Next: " + nextHearingDate : ""),
                notes,
                LocalDateTime.now(),
                DiaryVisibility.SHARED
        );

        caseAttentionService.evaluateAttentionRulesForCase(caseFile);

        return caseMapper.toHearingDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseHearingDto> getHearingsForCase(Long caseId) {
        return hearingRepository.findByCaseFileIdOrderByHearingDateDesc(caseId).stream()
                .map(caseMapper::toHearingDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseHearingDto> getUpcomingHearingsForCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) return List.of();

        if (SecurityUtils.isAdmin()) {
            return hearingRepository.findAllUpcomingHearings(LocalDate.now()).stream()
                    .map(caseMapper::toHearingDto)
                    .toList();
        }

        return hearingRepository.findUpcomingHearingsForUser(currentUserId, LocalDate.now()).stream()
                .map(caseMapper::toHearingDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseHearingDto> getAllUpcomingHearings() {
        return hearingRepository.findAllUpcomingHearings(LocalDate.now()).stream()
                .map(caseMapper::toHearingDto)
                .toList();
    }

    private void validateCaseAccess(CaseFile caseFile, Long userId) {
        if (SecurityUtils.isAdmin()) return;
        boolean isClient = caseFile.getClient() != null && caseFile.getClient().getId().equals(userId);
        boolean isLawyer = caseFile.getLawyer() != null && caseFile.getLawyer().getId().equals(userId);
        if (!isClient && !isLawyer) {
            throw new UnauthorizedAccessException("You are not authorized to manage hearings for this case");
        }
    }
}
