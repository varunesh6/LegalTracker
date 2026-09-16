package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.lawyer.CreateLawyerRequestDto;
import com.legaltrack.dto.lawyer.LawyerRequestDto;
import com.legaltrack.service.LawyerRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lawyer-requests")
@RequiredArgsConstructor
@Tag(name = "Lawyer Requests", description = "Client representation requests to lawyers")
public class LawyerRequestController {

    private final LawyerRequestService lawyerRequestService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Operation(summary = "Submit new lawyer representation request")
    public ResponseEntity<ApiResponse<LawyerRequestDto>> sendRequest(@Valid @RequestBody CreateLawyerRequestDto request) {
        LawyerRequestDto response = lawyerRequestService.sendRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Lawyer request sent successfully", response));
    }

    @GetMapping("/my")
    @Operation(summary = "Get requests sent or received by current user")
    public ResponseEntity<ApiResponse<PagedResponse<LawyerRequestDto>>> getMyRequests(Pageable pageable) {
        PagedResponse<LawyerRequestDto> response = lawyerRequestService.getMyRequests(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Requests retrieved", response));
    }

    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('LAWYER') or hasRole('ADMIN')")
    @Operation(summary = "Accept lawyer request and create client-lawyer relationship")
    public ResponseEntity<ApiResponse<LawyerRequestDto>> acceptRequest(@PathVariable Long id) {
        LawyerRequestDto response = lawyerRequestService.acceptRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request accepted and relationship created", response));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('LAWYER') or hasRole('ADMIN')")
    @Operation(summary = "Reject lawyer representation request")
    public ResponseEntity<ApiResponse<LawyerRequestDto>> rejectRequest(@PathVariable Long id) {
        LawyerRequestDto response = lawyerRequestService.rejectRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request rejected", response));
    }
}
