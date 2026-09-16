package com.legaltrack.service;

import com.legaltrack.dto.directory.*;

import java.util.List;

public interface CourtDirectoryService {
    List<StateDto> getAllStates();
    List<DistrictDto> getDistrictsByState(Long stateId);
    List<CourtComplexDto> getCourtComplexesByDistrict(Long districtId);
    List<CourtDto> getCourtsByCourtComplex(Long courtComplexId);
    List<CourtDto> getAllCourts();
    List<CaseTypeDto> getAllCaseTypes();
    List<PoliceStationDto> getPoliceStationsByDistrict(Long districtId);
}
