package com.legaltrack.service;

import com.legaltrack.dto.admin.CaseSyncLogDto;
import com.legaltrack.dto.admin.SystemStatsDto;
import com.legaltrack.dto.auth.UserProfileDto;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.enums.RoleType;
import com.legaltrack.enums.UserStatus;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    SystemStatsDto getSystemStats();
    PagedResponse<UserProfileDto> getUsers(RoleType role, Pageable pageable);
    void updateUserStatus(Long userId, UserStatus status);
    PagedResponse<CaseSyncLogDto> getSyncLogs(Pageable pageable);
}
