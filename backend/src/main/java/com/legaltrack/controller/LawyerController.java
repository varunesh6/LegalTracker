package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.lawyer.*;
import com.legaltrack.enums.LawyerAvailabilityStatus;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.LawyerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/lawyers")
@RequiredArgsConstructor
@Tag(name = "Lawyers", description = "Lawyer discovery, profile management, and availability")
public class LawyerController {

    private final LawyerService lawyerService;

    @GetMapping("/search")
    @Operation(summary = "Search lawyers with multi-criteria filters & pagination")
    public ResponseEntity<ApiResponse<PagedResponse<LawyerProfileDto>>> searchLawyers(
            @RequestParam(required = false) Long stateId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Boolean verifiedOnly,
            @RequestParam(required = false) LawyerAvailabilityStatus availability,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "experienceYears") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        LawyerSearchCriteria criteria = LawyerSearchCriteria.builder()
                .stateId(stateId)
                .districtId(districtId)
                .courtId(courtId)
                .specialization(specialization)
                .language(language)
                .minExperience(minExperience)
                .verifiedOnly(verifiedOnly)
                .availabilityStatus(availability)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PagedResponse<LawyerProfileDto> response = lawyerService.searchLawyers(criteria);
        return ResponseEntity.ok(ApiResponse.ok("Lawyers retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lawyer profile by profile ID")
    public ResponseEntity<ApiResponse<LawyerProfileDto>> getLawyerById(@PathVariable Long id) {
        LawyerProfileDto profile = lawyerService.getLawyerById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lawyer profile retrieved", profile));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('LAWYER') or hasRole('ADMIN')")
    @Operation(summary = "Update current lawyer profile details")
    public ResponseEntity<ApiResponse<LawyerProfileDto>> updateProfile(@RequestBody LawyerProfileDto profileDto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LawyerProfileDto updated = lawyerService.updateLawyerProfile(currentUserId, profileDto);
        return ResponseEntity.ok(ApiResponse.ok("Lawyer profile updated successfully", updated));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('LAWYER') or hasRole('ADMIN')")
    @Operation(summary = "Update lawyer availability status")
    public ResponseEntity<ApiResponse<LawyerAvailabilityDto>> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvailabilityRequest request
    ) {
        LawyerAvailabilityDto dto = lawyerService.updateAvailability(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Availability updated successfully", dto));
    }

    @PostMapping(value = "/{id}/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LAWYER') or hasRole('ADMIN')")
    @Operation(summary = "Upload verification certificate/document for lawyer verification")
    public ResponseEntity<ApiResponse<LawyerVerificationDto>> submitVerification(
            @PathVariable Long id,
            @RequestParam("document") MultipartFile document
    ) {
        LawyerVerificationDto dto = lawyerService.submitVerificationDocument(id, document);
        return ResponseEntity.ok(ApiResponse.ok("Verification document submitted successfully", dto));
    }

    @PatchMapping("/verifications/{verificationId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or reject lawyer verification (Admin only)")
    public ResponseEntity<ApiResponse<LawyerVerificationDto>> reviewVerification(
            @PathVariable Long verificationId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks
    ) {
        LawyerVerificationDto dto = lawyerService.reviewVerification(verificationId, approved, remarks);
        return ResponseEntity.ok(ApiResponse.ok("Verification review completed", dto));
    }
}
