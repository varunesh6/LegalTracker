package com.legaltrack.integration.court;

import com.legaltrack.dto.casefile.CaseFileDto;
import com.legaltrack.dto.casefile.CaseHearingDto;
import com.legaltrack.dto.casefile.CaseOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Architectural abstraction for official court API integration in future production rollout.
 * Clearly stubbed for academic demonstration without attempting unauthorized web scraping.
 */
@Slf4j
@Component("eCourtsProvider")
public class ECourtsProvider implements CourtDataProvider {

    @Override
    public Optional<CaseFileDto> searchByCnr(String cnrNumber) {
        log.info("ECourtsProvider: Official API endpoint placeholder called for CNR: {}", cnrNumber);
        return Optional.empty();
    }

    @Override
    public Optional<CaseFileDto> searchByCaseNumber(String caseNumber, Long courtId, Integer filingYear) {
        log.info("ECourtsProvider: Official API endpoint placeholder called for Case No: {}", caseNumber);
        return Optional.empty();
    }

    @Override
    public Optional<CaseFileDto> searchByFirNumber(String firNumber, Integer firYear, Long policeStationId) {
        log.info("ECourtsProvider: Official API endpoint placeholder called for FIR: {}", firNumber);
        return Optional.empty();
    }

    @Override
    public List<CaseHearingDto> getHearings(String cnrNumber) {
        return Collections.emptyList();
    }

    @Override
    public List<CaseOrderDto> getOrders(String cnrNumber) {
        return Collections.emptyList();
    }

    @Override
    public String getProviderName() {
        return "ECOURTS_OFFICIAL_API_STUB";
    }
}
