package com.legaltrack.service;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.support.*;
import com.legaltrack.enums.SupportStatus;
import org.springframework.data.domain.Pageable;

public interface SupportService {
    SupportTicketDto createTicket(CreateSupportTicketRequest request);
    SupportTicketDto getTicketById(Long ticketId);
    PagedResponse<SupportTicketDto> getMyTickets(Pageable pageable);
    PagedResponse<SupportTicketDto> getAllTickets(SupportStatus status, Pageable pageable);
    SupportMessageDto addMessageToTicket(Long ticketId, SendSupportMessageRequest request);
    SupportTicketDto updateTicketStatus(Long ticketId, UpdateSupportStatusRequest request);
}
