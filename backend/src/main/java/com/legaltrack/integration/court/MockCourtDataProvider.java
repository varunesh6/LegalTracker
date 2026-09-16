package com.legaltrack.integration.court;

import com.legaltrack.dto.casefile.CaseFileDto;
import com.legaltrack.dto.casefile.CaseHearingDto;
import com.legaltrack.dto.casefile.CaseOrderDto;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.repository.CaseFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("mockCourtDataProvider")
@RequiredArgsConstructor
public class MockCourtDataProvider implements CourtDataProvider {

    private final CaseFileRepository caseFileRepository;
    private final CaseMapper caseMapper;

    @Override
    public Optional<CaseFileDto> searchByCnr(String cnrNumber) {
        if (cnrNumber == null || cnrNumber.trim().isEmpty()) {
            return Optional.empty();
        }

        // Search local database demo records first
        Optional<CaseFile> dbCase = caseFileRepository.findByCnrNumber(cnrNumber.trim());
        if (dbCase.isPresent()) {
            return dbCase.map(caseMapper::toDto);
        }

        // Mock fallback if user tries another demo CNR
        if (cnrNumber.equalsIgnoreCase("DEMO345678") || cnrNumber.equalsIgnoreCase("DEMO999999")) {
            return Optional.of(CaseFileDto.builder()
                    .title("DEMO MOTOR ACCIDENT CLAIM (K. Velu vs National Insurance Co.)")
                    .cnrNumber(cnrNumber.toUpperCase())
                    .caseNumber("MCOP/204/2026")
                    .caseType("Motor Accident Claims")
                    .caseCategory("MOTOR_ACCIDENT")
                    .courtName("Principal District & Sessions Court, Salem")
                    .districtName("Salem")
                    .stateName("Tamil Nadu")
                    .status(com.legaltrack.enums.CaseStatus.PENDING)
                    .stage(com.legaltrack.enums.CaseStage.EVIDENCE)
                    .nextHearingDate(LocalDate.now().plusDays(10))
                    .isDemoData(true)
                    .matterDescription("DEMO DATA: Claim petition seeking compensation under Section 166 of MV Act.")
                    .build());
        }

        return Optional.empty();
    }

    @Override
    public Optional<CaseFileDto> searchByCaseNumber(String caseNumber, Long courtId, Integer filingYear) {
        if (caseNumber == null || caseNumber.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<CaseFile> dbCase = caseFileRepository.findByCaseNumber(caseNumber.trim());
        return dbCase.map(caseMapper::toDto);
    }

    @Override
    public Optional<CaseFileDto> searchByFirNumber(String firNumber, Integer firYear, Long policeStationId) {
        if (firNumber == null || firNumber.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<CaseFile> dbCase = caseFileRepository.findByFirNumber(firNumber.trim());
        return dbCase.map(caseMapper::toDto);
    }

    @Override
    public List<CaseHearingDto> getHearings(String cnrNumber) {
        Optional<CaseFile> dbCase = caseFileRepository.findByCnrNumber(cnrNumber);
        if (dbCase.isPresent()) {
            return dbCase.get().getHearings().stream()
                    .map(caseMapper::toHearingDto)
                    .toList();
        }
        return new ArrayList<>();
    }

    @Override
    public List<CaseOrderDto> getOrders(String cnrNumber) {
        Optional<CaseFile> dbCase = caseFileRepository.findByCnrNumber(cnrNumber);
        if (dbCase.isPresent()) {
            return dbCase.get().getOrders().stream()
                    .map(caseMapper::toOrderDto)
                    .toList();
        }
        return new ArrayList<>();
    }

    @Override
    public String getProviderName() {
        return "MOCK_COURT_DATA_PROVIDER";
    }
}
