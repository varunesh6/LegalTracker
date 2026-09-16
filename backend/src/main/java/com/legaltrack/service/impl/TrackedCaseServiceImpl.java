package com.legaltrack.service.impl;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.tracked.TrackCaseRequest;
import com.legaltrack.dto.tracked.TrackedCaseDto;
import com.legaltrack.dto.tracked.UpdateTrackedCaseRequest;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.TrackedCase;
import com.legaltrack.entity.User;
import com.legaltrack.exception.DuplicateResourceException;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.TrackedCaseMapper;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.TrackedCaseRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.TrackedCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackedCaseServiceImpl implements TrackedCaseService {

    private final TrackedCaseRepository trackedCaseRepository;
    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;
    private final TrackedCaseMapper trackedCaseMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public TrackedCaseDto trackCase(TrackCaseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(request.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", request.getCaseId()));

        if (trackedCaseRepository.existsByUserIdAndCaseFileId(currentUserId, request.getCaseId())) {
            throw new DuplicateResourceException("You are already tracking this case");
        }

        TrackedCase trackedCase = TrackedCase.builder()
                .user(currentUser)
                .caseFile(caseFile)
                .nickname(request.getNickname() != null ? request.getNickname() : caseFile.getTitle())
                .notificationsEnabled(request.getNotificationsEnabled() != null ? request.getNotificationsEnabled() : true)
                .trackedAt(LocalDateTime.now())
                .lastViewedAt(LocalDateTime.now())
                .build();

        TrackedCase saved = trackedCaseRepository.save(trackedCase);
        auditLogService.logAction(currentUser, "CASE_TRACK_START", "TrackedCase", saved.getId(), null, "Started tracking case " + caseFile.getTitle());

        return trackedCaseMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TrackedCaseDto> getMyTrackedCases(String query, Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Page<TrackedCase> page = trackedCaseRepository.searchUserTrackedCases(currentUserId, query, pageable);

        return PagedResponse.<TrackedCaseDto>builder()
                .content(page.getContent().stream().map(trackedCaseMapper::toDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackedCaseDto> getAllMyTrackedCases() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return trackedCaseRepository.findByUserId(currentUserId).stream()
                .map(trackedCaseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TrackedCaseDto updateTrackedCase(Long id, UpdateTrackedCaseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        TrackedCase trackedCase = trackedCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrackedCase", "id", id));

        if (!trackedCase.getUser().getId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Cannot modify tracked case of another user");
        }

        if (request.getNickname() != null) trackedCase.setNickname(request.getNickname());
        if (request.getNotificationsEnabled() != null) trackedCase.setNotificationsEnabled(request.getNotificationsEnabled());

        TrackedCase updated = trackedCaseRepository.save(trackedCase);
        return trackedCaseMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void stopTracking(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        TrackedCase trackedCase = trackedCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrackedCase", "id", id));

        if (!trackedCase.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot delete tracked case of another user");
        }

        trackedCaseRepository.delete(trackedCase);
        auditLogService.logAction(trackedCase.getUser(), "CASE_TRACK_STOP", "TrackedCase", id, null, "Stopped tracking case ID: " + trackedCase.getCaseFile().getId());
    }

    @Override
    @Transactional
    public void toggleNotifications(Long id, boolean enabled) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        TrackedCase trackedCase = trackedCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrackedCase", "id", id));

        if (!trackedCase.getUser().getId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Cannot modify tracked case of another user");
        }

        trackedCase.setNotificationsEnabled(enabled);
        trackedCaseRepository.save(trackedCase);
    }
}
