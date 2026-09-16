package com.legaltrack.service.impl;

import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.dto.lawyer.CreateLawyerRequestDto;
import com.legaltrack.dto.lawyer.LawyerRequestDto;
import com.legaltrack.entity.*;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.enums.RelationshipStatus;
import com.legaltrack.enums.RequestStatus;
import com.legaltrack.exception.ApiException;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.LawyerMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.LawyerRequestService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LawyerRequestServiceImpl implements LawyerRequestService {

    private final LawyerRequestRepository lawyerRequestRepository;
    private final ClientLawyerRelationshipRepository relationshipRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerMapper lawyerMapper;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public LawyerRequestDto sendRequest(CreateLawyerRequestDto request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User client = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        User lawyer = userRepository.findById(request.getLawyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer User", "id", request.getLawyerId()));

        Court court = request.getCourtId() != null ?
                courtRepository.findById(request.getCourtId()).orElse(null) : null;

        LawyerRequest req = LawyerRequest.builder()
                .client(client)
                .lawyer(lawyer)
                .caseType(request.getCaseType())
                .court(court)
                .message(request.getMessage())
                .status(RequestStatus.PENDING)
                .build();

        LawyerRequest saved = lawyerRequestRepository.save(req);

        // Notify lawyer
        notificationService.createNotification(
                lawyer,
                NotificationType.LAWYER_REQUEST,
                "New Legal Representation Request",
                client.getName() + " requested legal assistance for " + request.getCaseType(),
                "REQUEST",
                saved.getId(),
                "/lawyer/requests"
        );

        auditLogService.logAction(client, "LAWYER_REQUEST_CREATE", "LawyerRequest", saved.getId(), null, "Sent request to lawyer " + lawyer.getName());

        return lawyerMapper.toRequestDto(saved);
    }

    @Override
    @Transactional
    public LawyerRequestDto acceptRequest(Long requestId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LawyerRequest request = lawyerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerRequest", "id", requestId));

        if (!request.getLawyer().getId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot accept request directed to another lawyer");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ApiException("Request has already been processed with status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.ACCEPTED);
        LawyerRequest saved = lawyerRequestRepository.save(request);

        // Create ClientLawyerRelationship
        if (!relationshipRepository.existsByClientIdAndLawyerIdAndStatus(request.getClient().getId(), request.getLawyer().getId(), RelationshipStatus.ACTIVE)) {
            relationshipRepository.save(ClientLawyerRelationship.builder()
                    .client(request.getClient())
                    .lawyer(request.getLawyer())
                    .status(RelationshipStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // Initialize Conversation if not existing
        if (conversationRepository.findByClientIdAndLawyerId(request.getClient().getId(), request.getLawyer().getId()).isEmpty()) {
            conversationRepository.save(Conversation.builder()
                    .client(request.getClient())
                    .lawyer(request.getLawyer())
                    .build());
        }

        // Notify client
        notificationService.createNotification(
                request.getClient(),
                NotificationType.LAWYER_REQUEST_ACCEPTED,
                "Lawyer Accepted Your Request",
                request.getLawyer().getName() + " has accepted your legal representation request.",
                "REQUEST",
                saved.getId(),
                "/client/my-cases"
        );

        auditLogService.logAction(request.getLawyer(), "LAWYER_REQUEST_ACCEPT", "LawyerRequest", saved.getId(), null, "Accepted client request from " + request.getClient().getName());

        return lawyerMapper.toRequestDto(saved);
    }

    @Override
    @Transactional
    public LawyerRequestDto rejectRequest(Long requestId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LawyerRequest request = lawyerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("LawyerRequest", "id", requestId));

        if (!request.getLawyer().getId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot reject request directed to another lawyer");
        }

        request.setStatus(RequestStatus.REJECTED);
        LawyerRequest saved = lawyerRequestRepository.save(request);

        // Notify client
        notificationService.createNotification(
                request.getClient(),
                NotificationType.LAWYER_REQUEST_REJECTED,
                "Lawyer Request Update",
                request.getLawyer().getName() + " was unable to take up your request at this time.",
                "REQUEST",
                saved.getId(),
                "/client/find-lawyer"
        );

        auditLogService.logAction(request.getLawyer(), "LAWYER_REQUEST_REJECT", "LawyerRequest", saved.getId(), null, "Rejected client request from " + request.getClient().getName());

        return lawyerMapper.toRequestDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<LawyerRequestDto> getMyRequests(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Page<LawyerRequest> page;

        if (SecurityUtils.isLawyer()) {
            page = lawyerRequestRepository.findByLawyerId(currentUserId, pageable);
        } else {
            page = lawyerRequestRepository.findByClientId(currentUserId, pageable);
        }

        return PagedResponse.<LawyerRequestDto>builder()
                .content(page.getContent().stream().map(lawyerMapper::toRequestDto).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
