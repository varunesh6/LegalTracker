package com.legaltrack.service;

import com.legaltrack.dto.casefile.CaseHearingDto;
import com.legaltrack.dto.casefile.CreateHearingRequest;
import com.legaltrack.enums.HearingStatus;

import java.util.List;

public interface CaseHearingService {
    CaseHearingDto scheduleHearing(Long caseId, CreateHearingRequest request);
    CaseHearingDto updateHearingStatus(Long hearingId, HearingStatus status, String notes, java.time.LocalDate nextHearingDate);
    List<CaseHearingDto> getHearingsForCase(Long caseId);
    List<CaseHearingDto> getUpcomingHearingsForCurrentUser();
    List<CaseHearingDto> getAllUpcomingHearings();
}
