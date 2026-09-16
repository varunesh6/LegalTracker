package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.directory.*;
import com.legaltrack.service.CourtDirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Court Directory", description = "States, districts, court complexes, courts and case types metadata")
public class CourtDirectoryController {

    private final CourtDirectoryService directoryService;

    @GetMapping("/courts/states")
    @Operation(summary = "Get all supported states")
    public ResponseEntity<ApiResponse<List<StateDto>>> getStates() {
        List<StateDto> states = directoryService.getAllStates();
        return ResponseEntity.ok(ApiResponse.ok("States retrieved", states));
    }

    @GetMapping("/courts/districts")
    @Operation(summary = "Get districts for a state")
    public ResponseEntity<ApiResponse<List<DistrictDto>>> getDistricts(@RequestParam Long stateId) {
        List<DistrictDto> districts = directoryService.getDistrictsByState(stateId);
        return ResponseEntity.ok(ApiResponse.ok("Districts retrieved", districts));
    }

    @GetMapping("/courts/complexes")
    @Operation(summary = "Get court complexes for a district")
    public ResponseEntity<ApiResponse<List<CourtComplexDto>>> getCourtComplexes(@RequestParam Long districtId) {
        List<CourtComplexDto> complexes = directoryService.getCourtComplexesByDistrict(districtId);
        return ResponseEntity.ok(ApiResponse.ok("Court complexes retrieved", complexes));
    }

    @GetMapping("/courts")
    @Operation(summary = "Get courts (optionally filtered by court complex)")
    public ResponseEntity<ApiResponse<List<CourtDto>>> getCourts(@RequestParam(required = false) Long courtComplexId) {
        List<CourtDto> courts = courtComplexId != null ?
                directoryService.getCourtsByCourtComplex(courtComplexId) :
                directoryService.getAllCourts();
        return ResponseEntity.ok(ApiResponse.ok("Courts retrieved", courts));
    }

    @GetMapping("/case-types")
    @Operation(summary = "Get all recognized court case types")
    public ResponseEntity<ApiResponse<List<CaseTypeDto>>> getCaseTypes() {
        List<CaseTypeDto> types = directoryService.getAllCaseTypes();
        return ResponseEntity.ok(ApiResponse.ok("Case types retrieved", types));
    }

    @GetMapping("/courts/police-stations")
    @Operation(summary = "Get police stations in a district")
    public ResponseEntity<ApiResponse<List<PoliceStationDto>>> getPoliceStations(@RequestParam Long districtId) {
        List<PoliceStationDto> stations = directoryService.getPoliceStationsByDistrict(districtId);
        return ResponseEntity.ok(ApiResponse.ok("Police stations retrieved", stations));
    }
}
