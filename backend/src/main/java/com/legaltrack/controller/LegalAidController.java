package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.legalaid.*;
import com.legaltrack.enums.LegalAidStatus;
import com.legaltrack.service.LegalAidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/legal-aid")
@RequiredArgsConstructor
@Tag(name = "Legal-Aid Assistance", description = "Legal aid eligibility pre-check, application submission, document verification and lawyer assignment")
public class LegalAidController {

    private final LegalAidService legalAidService;

    @PostMapping("/pre-check")
    @Operation(summary = "Perform free legal-aid eligibility pre-check")
    public ResponseEntity<ApiResponse<LegalAidPreCheckResponse>> preCheck(@Valid @RequestBody LegalAidPreCheckRequest request) {
        LegalAidPreCheckResponse response = legalAidService.preCheckEligibility(request);
        return ResponseEntity.ok(ApiResponse.ok("Pre-check evaluation completed", response));
    }

    @PostMapping("/applications")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Operation(summary = "Submit new full Legal Aid application")
    public ResponseEntity<ApiResponse<LegalAidApplicationDto>> apply(@Valid @RequestBody CreateLegalAidApplicationRequest request) {
        LegalAidApplicationDto response = legalAidService.applyForLegalAid(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Application submitted successfully", response));
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Get legal aid application details by ID")
    public ResponseEntity<ApiResponse<LegalAidApplicationDto>> getApplication(@PathVariable Long id) {
        LegalAidApplicationDto response = legalAidService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.ok("Application details retrieved", response));
    }

    @GetMapping("/applications/my")
    @Operation(summary = "Get legal aid applications submitted by current client")
    public ResponseEntity<ApiResponse<PagedResponse<LegalAidApplicationDto>>> getMyApplications(Pageable pageable) {
        PagedResponse<LegalAidApplicationDto> response = legalAidService.getMyApplications(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Applications retrieved", response));
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('LEGAL_AID_OFFICER') or hasRole('ADMIN')")
    @Operation(summary = "Search legal aid applications (Legal Aid Officer / Admin)")
    public ResponseEntity<ApiResponse<PagedResponse<LegalAidApplicationDto>>> searchApplications(
            @RequestParam(required = false) LegalAidStatus status,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        PagedResponse<LegalAidApplicationDto> response = legalAidService.searchApplications(status, districtId, query, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Applications retrieved", response));
    }

    @PostMapping(value = "/applications/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload supporting document for legal aid application")
    public ResponseEntity<ApiResponse<LegalAidDocumentDto>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentName") String documentName,
            @RequestParam("documentType") String documentType
    ) {
        LegalAidDocumentDto doc = legalAidService.uploadApplicationDocument(id, file, documentName, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Document uploaded successfully", doc));
    }

    @PatchMapping("/applications/{id}/review")
    @PreAuthorize("hasRole('LEGAL_AID_OFFICER') or hasRole('ADMIN')")
    @Operation(summary = "Review and verify legal aid application status & documents")
    public ResponseEntity<ApiResponse<LegalAidApplicationDto>> reviewApplication(
            @PathVariable Long id,
            @Valid @RequestBody LegalAidVerificationRequest request
    ) {
        LegalAidApplicationDto response = legalAidService.reviewApplication(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Application review updated", response));
    }

    @PostMapping("/applications/{id}/assign-lawyer")
    @PreAuthorize("hasRole('LEGAL_AID_OFFICER') or hasRole('ADMIN')")
    @Operation(summary = "Assign lawyer and generate case workspace for approved application")
    public ResponseEntity<ApiResponse<LegalAidApplicationDto>> assignLawyer(
            @PathVariable Long id,
            @Valid @RequestBody AssignLawyerRequest request
    ) {
        LegalAidApplicationDto response = legalAidService.assignLawyer(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Lawyer assigned and case created", response));
    }
}
