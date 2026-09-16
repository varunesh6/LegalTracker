package com.legaltrack.service.impl;

import com.legaltrack.dto.directory.*;
import com.legaltrack.mapper.DirectoryMapper;
import com.legaltrack.repository.*;
import com.legaltrack.service.CourtDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourtDirectoryServiceImpl implements CourtDirectoryService {

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final CourtComplexRepository courtComplexRepository;
    private final CourtRepository courtRepository;
    private final CaseTypeRepository caseTypeRepository;
    private final PoliceStationRepository policeStationRepository;
    private final DirectoryMapper directoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StateDto> getAllStates() {
        return stateRepository.findAll().stream().map(directoryMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> getDistrictsByState(Long stateId) {
        return districtRepository.findByStateId(stateId).stream().map(directoryMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtComplexDto> getCourtComplexesByDistrict(Long districtId) {
        return courtComplexRepository.findByDistrictId(districtId).stream().map(directoryMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtDto> getCourtsByCourtComplex(Long courtComplexId) {
        return courtRepository.findByCourtComplexId(courtComplexId).stream().map(directoryMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtDto> getAllCourts() {
        return courtRepository.findAll().stream().map(directoryMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> getAllCaseTypes() {
        return caseTypeRepository.findAll().stream().map(directoryMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoliceStationDto> getPoliceStationsByDistrict(Long districtId) {
        return policeStationRepository.findByDistrictId(districtId).stream().map(directoryMapper::toDto).toList();
    }
}
