package com.legaltrack.service.impl;

import com.legaltrack.dto.chat.ConversationDto;
import com.legaltrack.dto.chat.MessageDto;
import com.legaltrack.dto.chat.SendMessageRequest;
import com.legaltrack.entity.*;
import com.legaltrack.enums.MessageType;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.ChatMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.ChatService;
import com.legaltrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CaseFileRepository caseFileRepository;
    private final CaseDocumentRepository documentRepository;
    private final ChatMapper chatMapper;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getMyConversations() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Conversation> conversations;

        if (SecurityUtils.isLawyer()) {
            conversations = conversationRepository.findByLawyerId(currentUserId);
        } else {
            conversations = conversationRepository.findByClientId(currentUserId);
        }

        List<ConversationDto> result = new ArrayList<>();
        for (Conversation conv : conversations) {
            List<Message> msgs = messageRepository.findByConversationIdAndDeletedAtIsNullOrderBySentAtAsc(conv.getId());
            Message lastMsg = msgs.isEmpty() ? null : msgs.get(msgs.size() - 1);
            Long unread = messageRepository.countUnreadMessages(conv.getId(), currentUserId);
            result.add(chatMapper.toConversationDto(conv, lastMsg, unread));
        }

        return result;
    }

    @Override
    @Transactional
    public ConversationDto getOrCreateConversation(Long lawyerId, Long caseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User client = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        User lawyer = userRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", lawyerId));

        CaseFile caseFile = caseId != null ? caseFileRepository.findById(caseId).orElse(null) : null;

        Optional<Conversation> existing = conversationRepository.findByClientIdAndLawyerIdAndCaseFileId(currentUserId, lawyerId, caseId);
        Conversation conv;
        if (existing.isPresent()) {
            conv = existing.get();
        } else {
            conv = conversationRepository.save(Conversation.builder()
                    .client(client)
                    .lawyer(lawyer)
                    .caseFile(caseFile)
                    .build());
        }

        return chatMapper.toConversationDto(conv, null, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getConversationMessages(Long conversationId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        validateConversationAccess(conv, currentUserId);

        return messageRepository.findByConversationIdAndDeletedAtIsNullOrderBySentAtAsc(conversationId).stream()
                .map(m -> chatMapper.toMessageDto(m, currentUserId))
                .toList();
    }

    @Override
    @Transactional
    public MessageDto sendMessage(Long conversationId, SendMessageRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        validateConversationAccess(conv, currentUserId);

        CaseDocument attachment = request.getAttachmentId() != null ?
                documentRepository.findById(request.getAttachmentId()).orElse(null) : null;

        Message message = Message.builder()
                .conversation(conv)
                .sender(currentUser)
                .messageType(request.getMessageType() != null ? request.getMessageType() : MessageType.TEXT)
                .content(request.getContent())
                .attachment(attachment)
                .sentAt(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);
        MessageDto dto = chatMapper.toMessageDto(saved, currentUserId);

        // Broadcast to WebSocket topic
        try {
            messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, dto);
        } catch (Exception ex) {
            log.debug("WebSocket broadcast skipped: {}", ex.getMessage());
        }

        // Notify recipient
        User recipient = conv.getClient().getId().equals(currentUserId) ? conv.getLawyer() : conv.getClient();
        notificationService.createNotification(
                recipient,
                NotificationType.MESSAGE_RECEIVED,
                "New Message from " + currentUser.getName(),
                request.getContent().length() > 60 ? request.getContent().substring(0, 60) + "..." : request.getContent(),
                "MESSAGE",
                saved.getId(),
                "/client/cases/" + (conv.getCaseFile() != null ? conv.getCaseFile().getId() : "1") + "/messages"
        );

        return dto;
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long conversationId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Message> unread = messageRepository.findByConversationIdAndDeletedAtIsNullOrderBySentAtAsc(conversationId);
        for (Message m : unread) {
            if (!m.getSender().getId().equals(currentUserId) && m.getReadAt() == null) {
                m.setReadAt(LocalDateTime.now());
                messageRepository.save(m);
            }
        }
    }

    private void validateConversationAccess(Conversation conv, Long userId) {
        if (SecurityUtils.isAdmin()) return;
        boolean isClient = conv.getClient().getId().equals(userId);
        boolean isLawyer = conv.getLawyer().getId().equals(userId);
        if (!isClient && !isLawyer) {
            throw new UnauthorizedAccessException("You are not a participant in this conversation");
        }
    }
}
