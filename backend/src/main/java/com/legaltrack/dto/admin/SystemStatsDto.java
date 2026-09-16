package com.legaltrack.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatsDto {
    private long totalUsers;
    private long totalClients;
    private long totalLawyers;
    private long verifiedLawyers;
    private long pendingLawyerVerifications;
    private long totalCases;
    private long activeCases;
    private long totalTrackedCases;
    private long totalLegalAidApplications;
    private long pendingLegalAidApplications;
    private long openSupportTickets;
    private long totalHearingsScheduled;
}
