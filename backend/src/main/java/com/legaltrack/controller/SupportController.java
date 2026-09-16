package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.support.*;
import com.legaltrack.enums.SupportStatus;
import com.legaltrack.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Tag(name = "Support Controller", description = "Endpoints for user support tickets and dispute escalation")
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/tickets")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new support or dispute ticket")
    public ResponseEntity<ApiResponse<SupportTicketDto>> createTicket(@Valid @RequestBody CreateSupportTicketRequest request) {
        SupportTicketDto ticket = supportService.createTicket(request);
        return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", ticket));
    }

    @GetMapping("/tickets/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support ticket details and full message thread")
    public ResponseEntity<ApiResponse<SupportTicketDto>> getTicketById(@PathVariable Long ticketId) {
        SupportTicketDto ticket = supportService.getTicketById(ticketId);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    @GetMapping("/tickets/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated list of tickets created by current user")
    public ResponseEntity<ApiResponse<PagedResponse<SupportTicketDto>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<SupportTicketDto> tickets = supportService.getMyTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @Operation(summary = "Get all support tickets across the platform (Staff only)")
    public ResponseEntity<ApiResponse<PagedResponse<SupportTicketDto>>> getAllTickets(
            @RequestParam(required = false) SupportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<SupportTicketDto> tickets = supportService.getAllTickets(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @PostMapping("/tickets/{ticketId}/messages")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Post a reply/message to an existing support ticket")
    public ResponseEntity<ApiResponse<SupportMessageDto>> addMessageToTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody SendSupportMessageRequest request) {
        SupportMessageDto message = supportService.addMessageToTicket(ticketId, request);
        return ResponseEntity.ok(ApiResponse.success("Message added", message));
    }

    @PatchMapping("/tickets/{ticketId}/status")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @Operation(summary = "Update support ticket status or assign agent (Staff only)")
    public ResponseEntity<ApiResponse<SupportTicketDto>> updateTicketStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateSupportStatusRequest request) {
        SupportTicketDto updated = supportService.updateTicketStatus(ticketId, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", updated));
    }
}
