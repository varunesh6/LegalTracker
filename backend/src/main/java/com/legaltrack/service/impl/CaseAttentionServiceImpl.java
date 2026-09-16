package com.legaltrack.service.impl;

import com.legaltrack.dto.casefile.CaseAttentionDto;
import com.legaltrack.entity.CaseAttention;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.CaseHearing;
import com.legaltrack.enums.HearingStatus;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.enums.Severity;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.repository.CaseAttentionRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.CaseAttentionService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseAttentionServiceImpl implements CaseAttentionService {

    private final CaseAttentionRepository caseAttentionRepository;
    private final CaseMapper caseMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void createAttention(CaseFile caseFile, String type, String title, String description, Severity severity, String actionUrl) {
        Optional<CaseAttention> existing = caseAttentionRepository.findByCaseFileIdAndTypeAndResolvedFalse(caseFile.getId(), type);
        if (existing.isPresent()) {
            CaseAttention att = existing.get();
            att.setTitle(title);
            att.setDescription(description);
            att.setSeverity(severity);
            att.setActionUrl(actionUrl);
            caseAttentionRepository.save(att);
            return;
        }

        CaseAttention attention = CaseAttention.builder()
                .caseFile(caseFile)
                .type(type)
                .title(title)
                .description(description)
                .severity(severity)
                .actionUrl(actionUrl)
                .resolved(false)
                .build();

        caseAttentionRepository.save(attention);

        // Notify client and lawyer
        if (caseFile.getClient() != null) {
            notificationService.createNotification(
                    caseFile.getClient(),
                    NotificationType.CASE_ATTENTION,
                    "Action Required: " + title,
                    description,
                    "CASE",
                    caseFile.getId(),
                    actionUrl != null ? actionUrl : "/client/cases/" + caseFile.getId()
            );
        }
        if (caseFile.getLawyer() != null) {
            notificationService.createNotification(
                    caseFile.getLawyer(),
                    NotificationType.CASE_ATTENTION,
                    "Case Attention: " + title,
                    description,
                    "CASE",
                    caseFile.getId(),
                    actionUrl != null ? actionUrl : "/lawyer/cases/" + caseFile.getId()
            );
        }
    }

    @Override
    @Transactional
    public void evaluateAttentionRulesForCase(CaseFile caseFile) {
        if (caseFile == null) return;

        LocalDate today = LocalDate.now();

        // Rule 1: Hearing within 3 days
        if (caseFile.getNextHearingDate() != null) {
            long daysUntilHearing = ChronoUnit.DAYS.between(today, caseFile.getNextHearingDate());
            if (daysUntilHearing >= 0 && daysUntilHearing <= 3) {
                createAttention(
                        caseFile,
                        "HEARING_SOON",
                        "Hearing in " + (daysUntilHearing == 0 ? "today" : daysUntilHearing + " day" + (daysUntilHearing > 1 ? "s" : "")),
                        "Scheduled hearing on " + caseFile.getNextHearingDate() + " requires legal preparation.",
                        Severity.ACTION_REQUIRED,
                        "/client/cases/" + caseFile.getId() + "/hearings"
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseAttentionDto> getAttentionForCase(Long caseId) {
        return caseAttentionRepository.findByCaseFileIdOrderByCreatedAtDesc(caseId).stream()
                .map(caseMapper::toAttentionDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseAttentionDto> getUnresolvedAttentionForCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) return List.of();

        return caseAttentionRepository.findUnresolvedAttentionForUser(currentUserId).stream()
                .map(caseMapper::toAttentionDto)
                .toList();
    }

    @Override
    @Transactional
    public void resolveAttention(Long attentionId) {
        CaseAttention attention = caseAttentionRepository.findById(attentionId)
                .orElseThrow(() -> new ResourceNotFoundException("CaseAttention", "id", attentionId));

        attention.setResolved(true);
        attention.setResolvedAt(LocalDateTime.now());
        caseAttentionRepository.save(attention);
    }
}
