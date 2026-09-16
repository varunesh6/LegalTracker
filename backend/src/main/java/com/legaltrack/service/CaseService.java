package com.legaltrack.service;

import com.legaltrack.dto.casefile.*;
import com.legaltrack.dto.common.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CaseService {
    CaseFileDto createCase(CreateCaseRequest request);
    CaseFileDto updateCase(Long caseId, UpdateCaseRequest request);
    CaseFileDto getCaseById(Long caseId);
    CaseDetailsDto getCaseDetails(Long caseId);
    CaseTimelineResponse getCaseTimeline(Long caseId);
    PagedResponse<CaseFileDto> searchCases(CaseSearchCriteria criteria);
    PagedResponse<CaseFileDto> getMyCases(Pageable pageable);

    // Notes
    CaseNoteDto addCaseNote(Long caseId, CreateNoteRequest request);
    List<CaseNoteDto> getCaseNotes(Long caseId);

    // Existing case tracking via CourtDataProvider
    CaseDetailsDto trackByCnr(TrackByCnrRequest request);
    CaseDetailsDto trackByCaseNumber(TrackByCaseNumberRequest request);
    CaseDetailsDto trackByFir(TrackByFirRequest request);
}
