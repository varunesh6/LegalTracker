package com.legaltrack.integration.court;

import com.legaltrack.dto.casefile.CaseFileDto;
import com.legaltrack.entity.*;
import com.legaltrack.enums.DiaryEntryType;
import com.legaltrack.enums.DiaryVisibility;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.enums.SyncStatus;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.CaseSyncLogRepository;
import com.legaltrack.repository.TrackedCaseRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.service.CaseAttentionService;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseSyncService {

    private final CaseFileRepository caseFileRepository;
    private final TrackedCaseRepository trackedCaseRepository;
    private final CaseSyncLogRepository syncLogRepository;
    private final CourtDataProvider mockCourtDataProvider;
    private final CaseDiaryService caseDiaryService;
    private final CaseAttentionService caseAttentionService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional
    public void synchronizeAllTrackedCases() {
        log.info("Starting background synchronization for all tracked cases...");
        List<TrackedCase> trackedCases = trackedCaseRepository.findAll();

        for (TrackedCase tc : trackedCases) {
            syncSingleCase(tc.getCaseFile());
        }
    }

    @Transactional
    public void syncSingleCase(CaseFile caseFile) {
        if (caseFile == null || caseFile.getCnrNumber() == null) return;

        LocalDateTime startTime = LocalDateTime.now();
        int recordsUpdated = 0;
        String error = null;
        SyncStatus status = SyncStatus.SUCCESS;

        try {
            Optional<CaseFileDto> externalData = mockCourtDataProvider.searchByCnr(caseFile.getCnrNumber());
            if (externalData.isPresent()) {
                CaseFileDto latest = externalData.get();

                // Detect hearing date change
                if (latest.getNextHearingDate() != null && !latest.getNextHearingDate().equals(caseFile.getNextHearingDate())) {
                    LocalDate oldHearing = caseFile.getNextHearingDate();
                    caseFile.setNextHearingDate(latest.getNextHearingDate());
                    recordsUpdated++;

                    User systemUser = userRepository.findById(1L).orElse(null);
                    caseDiaryService.recordAutomaticDiaryEntry(
                            caseFile,
                            systemUser,
                            DiaryEntryType.HEARING,
                            "Hearing Date Synchronized: " + latest.getNextHearingDate(),
                            "Updated hearing date from " + oldHearing + " to " + latest.getNextHearingDate() + " via court data sync.",
                            LocalDateTime.now(),
                            DiaryVisibility.SHARED
                    );

                    caseAttentionService.evaluateAttentionRulesForCase(caseFile);

                    // Notify tracking users with notifications enabled
                    List<TrackedCase> trackers = trackedCaseRepository.findByCaseFileId(caseFile.getId());
                    for (TrackedCase tracker : trackers) {
                        if (tracker.getNotificationsEnabled()) {
                            notificationService.createNotification(
                                    tracker.getUser(),
                                    NotificationType.CASE_UPDATE,
                                    "Hearing Date Changed",
                                    "Next hearing for " + caseFile.getTitle() + " has been updated to " + latest.getNextHearingDate(),
                                    "CASE",
                                    caseFile.getId(),
                                    "/client/cases/" + caseFile.getId() + "/hearings"
                            );
                        }
                    }
                }

                caseFileRepository.save(caseFile);
            }
        } catch (Exception ex) {
            log.error("Failed to synchronize case ID {}: {}", caseFile.getId(), ex.getMessage());
            status = SyncStatus.FAILED;
            error = ex.getMessage();
        } finally {
            CaseSyncLog syncLog = CaseSyncLog.builder()
                    .caseFile(caseFile)
                    .startedAt(startTime)
                    .completedAt(LocalDateTime.now())
                    .status(status)
                    .recordsUpdated(recordsUpdated)
                    .errorMessage(error)
                    .source(mockCourtDataProvider.getProviderName())
                    .build();

            syncLogRepository.save(syncLog);
        }
    }
}
