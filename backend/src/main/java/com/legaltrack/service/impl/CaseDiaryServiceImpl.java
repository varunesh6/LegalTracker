package com.legaltrack.service.impl;

import com.legaltrack.dto.casefile.CaseDiaryEntryDto;
import com.legaltrack.dto.casefile.CreateDiaryEntryRequest;
import com.legaltrack.entity.CaseDiaryEntry;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.User;
import com.legaltrack.enums.DiaryEntryType;
import com.legaltrack.enums.DiaryVisibility;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.repository.CaseDiaryEntryRepository;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseDiaryServiceImpl implements CaseDiaryService {

    private final CaseDiaryEntryRepository diaryEntryRepository;
    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CaseDiaryEntryDto addDiaryEntry(Long caseId, CreateDiaryEntryRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        validateCaseAccess(caseFile, currentUserId);

        CaseDiaryEntry entry = CaseDiaryEntry.builder()
                .caseFile(caseFile)
                .createdBy(currentUser)
                .entryType(request.getEntryType())
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate() != null ? request.getEventDate() : LocalDateTime.now())
                .visibility(request.getVisibility() != null ? request.getVisibility() : DiaryVisibility.SHARED)
                .build();

        CaseDiaryEntry saved = diaryEntryRepository.save(entry);

        // Notify other party if shared
        if (entry.getVisibility() == DiaryVisibility.SHARED) {
            if (caseFile.getClient() != null && !caseFile.getClient().getId().equals(currentUserId)) {
                notificationService.createNotification(
                        caseFile.getClient(),
                        NotificationType.CASE_UPDATE,
                        "New Case Diary Entry",
                        entry.getTitle(),
                        "CASE",
                        caseFile.getId(),
                        "/client/cases/" + caseFile.getId() + "/diary"
                );
            }
            if (caseFile.getLawyer() != null && !caseFile.getLawyer().getId().equals(currentUserId)) {
                notificationService.createNotification(
                        caseFile.getLawyer(),
                        NotificationType.CASE_UPDATE,
                        "New Case Diary Entry",
                        entry.getTitle(),
                        "CASE",
                        caseFile.getId(),
                        "/lawyer/cases/" + caseFile.getId() + "/diary"
                );
            }
        }

        return caseMapper.toDiaryDto(saved);
    }

    @Override
    @Transactional
    public void recordAutomaticDiaryEntry(CaseFile caseFile, User actor, DiaryEntryType type, String title, String description, LocalDateTime eventDate, DiaryVisibility visibility) {
        if (caseFile == null || actor == null) return;

        CaseDiaryEntry entry = CaseDiaryEntry.builder()
                .caseFile(caseFile)
                .createdBy(actor)
                .entryType(type)
                .title(title)
                .description(description)
                .eventDate(eventDate != null ? eventDate : LocalDateTime.now())
                .visibility(visibility != null ? visibility : DiaryVisibility.SHARED)
                .build();

        diaryEntryRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseDiaryEntryDto> getDiaryEntriesForCase(Long caseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (SecurityUtils.isAdmin()) {
            return diaryEntryRepository.findByCaseFileIdOrderByEventDateDesc(caseId).stream()
                    .map(caseMapper::toDiaryDto)
                    .toList();
        }

        return diaryEntryRepository.findAuthorizedDiaryEntries(caseId, currentUserId).stream()
                .map(caseMapper::toDiaryDto)
                .toList();
    }

    private void validateCaseAccess(CaseFile caseFile, Long userId) {
        if (SecurityUtils.isAdmin()) return;
        boolean isClient = caseFile.getClient() != null && caseFile.getClient().getId().equals(userId);
        boolean isLawyer = caseFile.getLawyer() != null && caseFile.getLawyer().getId().equals(userId);
        if (!isClient && !isLawyer) {
            throw new UnauthorizedAccessException("You are not authorized to update diary for this case");
        }
    }
}
