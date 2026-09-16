package com.legaltrack.service.impl;

import com.legaltrack.dto.admin.CaseSyncLogDto;
import com.legaltrack.dto.admin.SystemStatsDto;
import com.legaltrack.dto.auth.UserProfileDto;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.entity.CaseSyncLog;
import com.legaltrack.entity.ClientProfile;
import com.legaltrack.entity.LawyerProfile;
import com.legaltrack.entity.User;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.enums.LegalAidStatus;
import com.legaltrack.enums.RoleType;
import com.legaltrack.enums.SupportStatus;
import com.legaltrack.enums.UserStatus;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.mapper.AuditMapper;
import com.legaltrack.mapper.UserMapper;
import com.legaltrack.repository.*;
import com.legaltrack.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final CaseFileRepository caseFileRepository;
    private final TrackedCaseRepository trackedCaseRepository;
    private final LegalAidApplicationRepository legalAidAppRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final CaseHearingRepository hearingRepository;
    private final CaseSyncLogRepository syncLogRepository;
    private final UserMapper userMapper;
    private final AuditMapper auditMapper;

    @Override
    @Transactional(readOnly = true)
    public SystemStatsDto getSystemStats() {
        long totalUsers = userRepository.count();
        long totalLawyers = lawyerProfileRepository.count();
        long verifiedLawyers = lawyerProfileRepository.findAll().stream().filter(LawyerProfile::getVerified).count();
        long totalCases = caseFileRepository.count();
        long activeCases = caseFileRepository.countByStatus(CaseStatus.PENDING) + caseFileRepository.countByStatus(CaseStatus.LISTED);
        long totalTracked = trackedCaseRepository.count();
        long totalLegalAid = legalAidAppRepository.count();
        long pendingLegalAid = legalAidAppRepository.countByStatus(LegalAidStatus.SUBMITTED) + legalAidAppRepository.countByStatus(LegalAidStatus.UNDER_REVIEW);
        long openSupport = supportTicketRepository.countByStatus(SupportStatus.OPEN);
        long totalHearings = hearingRepository.findAllUpcomingHearings(LocalDate.now()).size();

        return SystemStatsDto.builder()
                .totalUsers(totalUsers)
                .totalClients(totalUsers - totalLawyers)
                .totalLawyers(totalLawyers)
                .verifiedLawyers(verifiedLawyers)
                .pendingLawyerVerifications(totalLawyers - verifiedLawyers)
                .totalCases(totalCases)
                .activeCases(activeCases)
                .totalTrackedCases(totalTracked)
                .totalLegalAidApplications(totalLegalAid)
                .pendingLegalAidApplications(pendingLegalAid)
                .openSupportTickets(openSupport)
                .totalHearingsScheduled(totalHearings)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserProfileDto> getUsers(RoleType role, Pageable pageable) {
        Page<User> page = role != null ?
                userRepository.findByRoleName(role, pageable) :
                userRepository.findAll(pageable);

        return PagedResponse.<UserProfileDto>builder()
                .content(page.getContent().stream().map(u -> {
                    ClientProfile cp = clientProfileRepository.findByUserId(u.getId()).orElse(null);
                    LawyerProfile lp = lawyerProfileRepository.findByUserId(u.getId()).orElse(null);
                    return userMapper.toProfileDto(u, cp, lp);
                }).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CaseSyncLogDto> getSyncLogs(Pageable pageable) {
        Page<CaseSyncLog> page = syncLogRepository.findAllByOrderByStartedAtDesc(pageable);
        return PagedResponse.<CaseSyncLogDto>builder()
                .content(page.getContent().stream().map(auditMapper::toSyncLogDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
