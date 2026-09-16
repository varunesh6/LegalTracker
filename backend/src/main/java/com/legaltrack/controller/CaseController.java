package com.legaltrack.controller;

import com.legaltrack.dto.casefile.*;
import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.enums.HearingStatus;
import com.legaltrack.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Tag(name = "Case Workspace & Tracking", description = "Core Case Management, CNR search, hearings, orders, diary, attention and timeline")
public class CaseController {

    private final CaseService caseService;
    private final CaseDiaryService caseDiaryService;
    private final CaseHearingService caseHearingService;
    private final CaseOrderService caseOrderService;
    private final CaseAttentionService caseAttentionService;

    @PostMapping
    @Operation(summary = "Create a new legal case file")
    public ResponseEntity<ApiResponse<CaseFileDto>> createCase(@Valid @RequestBody CreateCaseRequest request) {
        CaseFileDto response = caseService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Case created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get aggregated case details and workspace by case ID")
    public ResponseEntity<ApiResponse<CaseDetailsDto>> getCaseDetails(@PathVariable Long id) {
        CaseDetailsDto response = caseService.getCaseDetails(id);
        return ResponseEntity.ok(ApiResponse.ok("Case details retrieved", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update case information, status or stage")
    public ResponseEntity<ApiResponse<CaseFileDto>> updateCase(@PathVariable Long id, @RequestBody UpdateCaseRequest request) {
        CaseFileDto response = caseService.updateCase(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Case updated successfully", response));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Get dynamic chronological case timeline")
    public ResponseEntity<ApiResponse<CaseTimelineResponse>> getCaseTimeline(@PathVariable Long id) {
        CaseTimelineResponse response = caseService.getCaseTimeline(id);
        return ResponseEntity.ok(ApiResponse.ok("Case timeline retrieved", response));
    }

    @GetMapping
    @Operation(summary = "Search cases with criteria, query, role & pagination")
    public ResponseEntity<ApiResponse<PagedResponse<CaseFileDto>>> searchCases(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long lawyerId,
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        CaseSearchCriteria criteria = CaseSearchCriteria.builder()
                .query(query)
                .clientId(clientId)
                .lawyerId(lawyerId)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PagedResponse<CaseFileDto> response = caseService.searchCases(criteria);
        return ResponseEntity.ok(ApiResponse.ok("Cases retrieved", response));
    }

    @GetMapping("/my")
    @Operation(summary = "Get active cases assigned to current client/lawyer")
    public ResponseEntity<ApiResponse<PagedResponse<CaseFileDto>>> getMyCases(Pageable pageable) {
        PagedResponse<CaseFileDto> response = caseService.getMyCases(pageable);
        return ResponseEntity.ok(ApiResponse.ok("My cases retrieved", response));
    }

    // --- CNR, Case No, FIR Search Endpoints ---
    @PostMapping("/track/cnr")
    @Operation(summary = "Search & track existing court case by CNR Number (via Mock Court Provider)")
    public ResponseEntity<ApiResponse<CaseDetailsDto>> trackByCnr(@Valid @RequestBody TrackByCnrRequest request) {
        CaseDetailsDto response = caseService.trackByCnr(request);
        return ResponseEntity.ok(ApiResponse.ok("Case search completed via court provider", response));
    }

    @PostMapping("/track/case-number")
    @Operation(summary = "Search case by Court Case Number")
    public ResponseEntity<ApiResponse<CaseDetailsDto>> trackByCaseNumber(@Valid @RequestBody TrackByCaseNumberRequest request) {
        CaseDetailsDto response = caseService.trackByCaseNumber(request);
        return ResponseEntity.ok(ApiResponse.ok("Case search completed", response));
    }

    @PostMapping("/track/fir")
    @Operation(summary = "Search case by Police Station FIR Number")
    public ResponseEntity<ApiResponse<CaseDetailsDto>> trackByFir(@Valid @RequestBody TrackByFirRequest request) {
        CaseDetailsDto response = caseService.trackByFir(request);
        return ResponseEntity.ok(ApiResponse.ok("Case search completed", response));
    }

    // --- Case Diary ---
    @GetMapping("/{id}/diary")
    @Operation(summary = "Get chronological Case Diary entries for a case")
    public ResponseEntity<ApiResponse<List<CaseDiaryEntryDto>>> getCaseDiary(@PathVariable Long id) {
        List<CaseDiaryEntryDto> diary = caseDiaryService.getDiaryEntriesForCase(id);
        return ResponseEntity.ok(ApiResponse.ok("Case diary retrieved", diary));
    }

    @PostMapping("/{id}/diary")
    @Operation(summary = "Add an entry to the Case Diary")
    public ResponseEntity<ApiResponse<CaseDiaryEntryDto>> addDiaryEntry(
            @PathVariable Long id,
            @Valid @RequestBody CreateDiaryEntryRequest request
    ) {
        CaseDiaryEntryDto entry = caseDiaryService.addDiaryEntry(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Diary entry recorded", entry));
    }

    // --- Hearings ---
    @GetMapping("/{id}/hearings")
    @Operation(summary = "Get all hearings for a case")
    public ResponseEntity<ApiResponse<List<CaseHearingDto>>> getHearings(@PathVariable Long id) {
        List<CaseHearingDto> hearings = caseHearingService.getHearingsForCase(id);
        return ResponseEntity.ok(ApiResponse.ok("Hearings retrieved", hearings));
    }

    @PostMapping("/{id}/hearings")
    @Operation(summary = "Schedule a hearing for a case")
    public ResponseEntity<ApiResponse<CaseHearingDto>> scheduleHearing(
            @PathVariable Long id,
            @Valid @RequestBody CreateHearingRequest request
    ) {
        CaseHearingDto hearing = caseHearingService.scheduleHearing(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Hearing scheduled successfully", hearing));
    }

    @PatchMapping("/hearings/{hearingId}/status")
    @Operation(summary = "Update hearing outcome status (SCHEDULED, COMPLETED, ADJOURNED)")
    public ResponseEntity<ApiResponse<CaseHearingDto>> updateHearingStatus(
            @PathVariable Long hearingId,
            @RequestParam HearingStatus status,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) LocalDate nextHearingDate
    ) {
        CaseHearingDto hearing = caseHearingService.updateHearingStatus(hearingId, status, notes, nextHearingDate);
        return ResponseEntity.ok(ApiResponse.ok("Hearing status updated", hearing));
    }

    @GetMapping("/hearings/upcoming")
    @Operation(summary = "Get upcoming hearings for current user")
    public ResponseEntity<ApiResponse<List<CaseHearingDto>>> getUpcomingHearings() {
        List<CaseHearingDto> hearings = caseHearingService.getUpcomingHearingsForCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Upcoming hearings retrieved", hearings));
    }

    // --- Orders ---
    @GetMapping("/{id}/orders")
    @Operation(summary = "Get court orders for a case")
    public ResponseEntity<ApiResponse<List<CaseOrderDto>>> getOrders(@PathVariable Long id) {
        List<CaseOrderDto> orders = caseOrderService.getOrdersForCase(id);
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved", orders));
    }

    @PostMapping("/{id}/orders")
    @Operation(summary = "Record a new court order for a case")
    public ResponseEntity<ApiResponse<CaseOrderDto>> addOrder(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        CaseOrderDto order = caseOrderService.addOrder(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Order recorded successfully", order));
    }

    // --- Attention Items ---
    @GetMapping("/{id}/attention")
    @Operation(summary = "Get administrative Case Attention items for a case")
    public ResponseEntity<ApiResponse<List<CaseAttentionDto>>> getAttention(@PathVariable Long id) {
        List<CaseAttentionDto> items = caseAttentionService.getAttentionForCase(id);
        return ResponseEntity.ok(ApiResponse.ok("Case attention items retrieved", items));
    }

    @GetMapping("/attention/my")
    @Operation(summary = "Get unresolved Case Attention alerts for current user dashboard")
    public ResponseEntity<ApiResponse<List<CaseAttentionDto>>> getMyAttentionItems() {
        List<CaseAttentionDto> items = caseAttentionService.getUnresolvedAttentionForCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Unresolved attention items retrieved", items));
    }

    @PatchMapping("/attention/{attentionId}/resolve")
    @Operation(summary = "Mark a Case Attention item as resolved")
    public ResponseEntity<ApiResponse<Void>> resolveAttention(@PathVariable Long attentionId) {
        caseAttentionService.resolveAttention(attentionId);
        return ResponseEntity.ok(ApiResponse.ok("Attention item resolved"));
    }

    // --- Notes ---
    @PostMapping("/{id}/notes")
    @Operation(summary = "Add private or shared note to a case")
    public ResponseEntity<ApiResponse<CaseNoteDto>> addNote(
            @PathVariable Long id,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        CaseNoteDto note = caseService.addCaseNote(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Note added", note));
    }

    @GetMapping("/{id}/notes")
    @Operation(summary = "Get authorized notes for a case")
    public ResponseEntity<ApiResponse<List<CaseNoteDto>>> getNotes(@PathVariable Long id) {
        List<CaseNoteDto> notes = caseService.getCaseNotes(id);
        return ResponseEntity.ok(ApiResponse.ok("Notes retrieved", notes));
    }
}
