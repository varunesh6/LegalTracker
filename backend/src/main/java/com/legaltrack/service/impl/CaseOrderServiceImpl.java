package com.legaltrack.service.impl;

import com.legaltrack.dto.casefile.CaseOrderDto;
import com.legaltrack.dto.casefile.CreateOrderRequest;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.CaseOrder;
import com.legaltrack.entity.User;
import com.legaltrack.enums.DiaryEntryType;
import com.legaltrack.enums.DiaryVisibility;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.enums.Severity;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.CaseOrderRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.CaseAttentionService;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.CaseOrderService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseOrderServiceImpl implements CaseOrderService {

    private final CaseOrderRepository orderRepository;
    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final CaseDiaryService caseDiaryService;
    private final CaseAttentionService caseAttentionService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CaseOrderDto addOrder(Long caseId, CreateOrderRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        validateCaseAccess(caseFile, currentUserId);

        CaseOrder order = CaseOrder.builder()
                .caseFile(caseFile)
                .orderDate(request.getOrderDate())
                .title(request.getTitle())
                .orderType(request.getOrderType())
                .documentId(request.getDocumentId())
                .summary(request.getSummary())
                .source(SecurityUtils.isAdmin() ? "ADMIN" : "USER")
                .build();

        CaseOrder saved = orderRepository.save(order);

        // Record automatic case diary entry
        caseDiaryService.recordAutomaticDiaryEntry(
                caseFile,
                currentUser,
                DiaryEntryType.ORDER,
                "New Order: " + request.getTitle(),
                request.getSummary(),
                LocalDateTime.now(),
                DiaryVisibility.SHARED
        );

        // Create Case Attention item
        caseAttentionService.createAttention(
                caseFile,
                "NEW_ORDER_AVAILABLE",
                "New order available",
                request.getTitle() + " dated " + request.getOrderDate() + " has been added to the case.",
                Severity.INFO,
                "/client/cases/" + caseFile.getId() + "/orders"
        );

        // Notify client and lawyer
        String notif = "New order '" + request.getTitle() + "' has been recorded for " + caseFile.getTitle();
        if (caseFile.getClient() != null) {
            notificationService.createNotification(caseFile.getClient(), NotificationType.ORDER_AVAILABLE, "New Order Available", notif, "ORDER", saved.getId(), "/client/cases/" + caseFile.getId() + "/orders");
        }
        if (caseFile.getLawyer() != null) {
            notificationService.createNotification(caseFile.getLawyer(), NotificationType.ORDER_AVAILABLE, "New Order Recorded", notif, "ORDER", saved.getId(), "/lawyer/cases/" + caseFile.getId() + "/orders");
        }

        return caseMapper.toOrderDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseOrderDto> getOrdersForCase(Long caseId) {
        return orderRepository.findByCaseFileIdOrderByOrderDateDesc(caseId).stream()
                .map(caseMapper::toOrderDto)
                .toList();
    }

    private void validateCaseAccess(CaseFile caseFile, Long userId) {
        if (SecurityUtils.isAdmin()) return;
        boolean isClient = caseFile.getClient() != null && caseFile.getClient().getId().equals(userId);
        boolean isLawyer = caseFile.getLawyer() != null && caseFile.getLawyer().getId().equals(userId);
        if (!isClient && !isLawyer) {
            throw new UnauthorizedAccessException("You are not authorized to add orders to this case");
        }
    }
}
