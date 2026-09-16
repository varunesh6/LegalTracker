package com.legaltrack.mapper;

import com.legaltrack.dto.directory.*;
import com.legaltrack.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class DirectoryMapper {

    public StateDto toDto(State state) {
        if (state == null) return null;
        return StateDto.builder()
                .id(state.getId())
                .name(state.getName())
                .code(state.getCode())
                .build();
    }

    public DistrictDto toDto(District district) {
        if (district == null) return null;
        return DistrictDto.builder()
                .id(district.getId())
                .stateId(district.getState() != null ? district.getState().getId() : null)
                .stateName(district.getState() != null ? district.getState().getName() : null)
                .name(district.getName())
                .code(district.getCode())
                .build();
    }

    public CourtComplexDto toDto(CourtComplex complex) {
        if (complex == null) return null;
        return CourtComplexDto.builder()
                .id(complex.getId())
                .districtId(complex.getDistrict() != null ? complex.getDistrict().getId() : null)
                .districtName(complex.getDistrict() != null ? complex.getDistrict().getName() : null)
                .name(complex.getName())
                .address(complex.getAddress())
                .build();
    }

    public CourtDto toDto(Court court) {
        if (court == null) return null;
        return CourtDto.builder()
                .id(court.getId())
                .courtComplexId(court.getCourtComplex() != null ? court.getCourtComplex().getId() : null)
                .courtComplexName(court.getCourtComplex() != null ? court.getCourtComplex().getName() : null)
                .name(court.getName())
                .courtType(court.getCourtType())
                .judgeDesignation(court.getJudgeDesignation())
                .build();
    }

    public CaseTypeDto toDto(CaseType caseType) {
        if (caseType == null) return null;
        return CaseTypeDto.builder()
                .id(caseType.getId())
                .name(caseType.getName())
                .code(caseType.getCode())
                .category(caseType.getCategory())
                .description(caseType.getDescription())
                .build();
    }

    public PoliceStationDto toDto(PoliceStation ps) {
        if (ps == null) return null;
        return PoliceStationDto.builder()
                .id(ps.getId())
                .districtId(ps.getDistrict() != null ? ps.getDistrict().getId() : null)
                .districtName(ps.getDistrict() != null ? ps.getDistrict().getName() : null)
                .name(ps.getName())
                .code(ps.getCode())
                .build();
    }
}
