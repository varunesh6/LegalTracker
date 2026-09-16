package com.legaltrack.service.impl;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.support.*;
import com.legaltrack.entity.SupportMessage;
import com.legaltrack.entity.SupportTicket;
import com.legaltrack.entity.User;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.enums.SupportStatus;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.SupportMapper;
import com.legaltrack.repository.SupportMessageRepository;
import com.legaltrack.repository.SupportTicketRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.NotificationService;
import com.legaltrack.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final SupportTicketRepository ticketRepository;
    private final SupportMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SupportMapper supportMapper;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SupportTicketDto createTicket(CreateSupportTicketRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        String ticketNumber = "TKT-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber(ticketNumber)
                .user(currentUser)
                .category(request.getCategory())
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(SupportStatus.OPEN)
                .build();

        SupportTicket saved = ticketRepository.save(ticket);

        // Add initial message
        messageRepository.save(SupportMessage.builder()
                .ticket(saved)
                .sender(currentUser)
                .message(request.getDescription())
                .build());

        auditLogService.logAction(currentUser, "SUPPORT_TICKET_CREATE", "SupportTicket", saved.getId(), null, "Created ticket " + ticketNumber);

        return supportMapper.toTicketDto(saved, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportTicketDto getTicketById(Long ticketId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        if (!ticket.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdmin() && !SecurityUtils.hasRole("ROLE_SUPPORT")) {
            throw new UnauthorizedAccessException("Cannot view support ticket of another user");
        }

        return supportMapper.toTicketDto(ticket, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupportTicketDto> getMyTickets(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Page<SupportTicket> page = ticketRepository.findByUserId(currentUserId, pageable);

        return PagedResponse.<SupportTicketDto>builder()
                .content(page.getContent().stream().map(t -> supportMapper.toTicketDto(t, currentUserId)).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupportTicketDto> getAllTickets(SupportStatus status, Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Page<SupportTicket> page = status != null ?
                ticketRepository.findByStatus(status, pageable) :
                ticketRepository.findAll(pageable);

        return PagedResponse.<SupportTicketDto>builder()
                .content(page.getContent().stream().map(t -> supportMapper.toTicketDto(t, currentUserId)).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public SupportMessageDto addMessageToTicket(Long ticketId, SendSupportMessageRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        if (!ticket.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdmin() && !SecurityUtils.hasRole("ROLE_SUPPORT")) {
            throw new UnauthorizedAccessException("Cannot reply to support ticket of another user");
        }

        SupportMessage message = SupportMessage.builder()
                .ticket(ticket)
                .sender(currentUser)
                .message(request.getMessage())
                .build();

        SupportMessage saved = messageRepository.save(message);

        // If support replied, notify user
        if (!ticket.getUser().getId().equals(currentUserId)) {
            ticket.setStatus(SupportStatus.WAITING_FOR_USER);
            notificationService.createNotification(
                    ticket.getUser(),
                    NotificationType.SUPPORT_UPDATE,
                    "Support Ticket Reply",
                    "Support agent replied to ticket #" + ticket.getTicketNumber(),
                    "SUPPORT",
                    ticket.getId(),
                    "/client/support"
            );
        } else {
            ticket.setStatus(SupportStatus.IN_PROGRESS);
        }
        ticketRepository.save(ticket);

        return supportMapper.toMessageDto(saved, currentUserId);
    }

    @Override
    @Transactional
    public SupportTicketDto updateTicketStatus(Long ticketId, UpdateSupportStatusRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        ticket.setStatus(request.getStatus());
        SupportTicket updated = ticketRepository.save(ticket);

        notificationService.createNotification(
                ticket.getUser(),
                NotificationType.SUPPORT_UPDATE,
                "Support Ticket Status Updated",
                "Your support ticket #" + ticket.getTicketNumber() + " is now " + request.getStatus(),
                "SUPPORT",
                ticket.getId(),
                "/client/support"
        );

        return supportMapper.toTicketDto(updated, currentUserId);
    }
}
