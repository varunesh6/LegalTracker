package com.legaltrack.integration.court;

import com.legaltrack.dto.casefile.CaseFileDto;
import com.legaltrack.dto.casefile.CaseHearingDto;
import com.legaltrack.dto.casefile.CaseOrderDto;

import java.util.List;
import java.util.Optional;

public interface CourtDataProvider {
    Optional<CaseFileDto> searchByCnr(String cnrNumber);
    Optional<CaseFileDto> searchByCaseNumber(String caseNumber, Long courtId, Integer filingYear);
    Optional<CaseFileDto> searchByFirNumber(String firNumber, Integer firYear, Long policeStationId);
    List<CaseHearingDto> getHearings(String cnrNumber);
    List<CaseOrderDto> getOrders(String cnrNumber);
    String getProviderName();
}
