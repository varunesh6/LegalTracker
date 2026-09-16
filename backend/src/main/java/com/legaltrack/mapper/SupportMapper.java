package com.legaltrack.mapper;

import com.legaltrack.dto.support.SupportMessageDto;
import com.legaltrack.dto.support.SupportTicketDto;
import com.legaltrack.entity.SupportMessage;
import com.legaltrack.entity.SupportTicket;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class SupportMapper {

    public SupportTicketDto toTicketDto(SupportTicket ticket, Long currentUserId) {
        if (ticket == null) return null;

        return SupportTicketDto.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .userId(ticket.getUser().getId())
                .userName(ticket.getUser().getName())
                .userEmail(ticket.getUser().getEmail())
                .category(ticket.getCategory())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .messages(ticket.getMessages() != null ?
                        ticket.getMessages().stream().map(m -> toMessageDto(m, currentUserId)).collect(Collectors.toList()) :
                        Collections.emptyList())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public SupportMessageDto toMessageDto(SupportMessage msg, Long currentUserId) {
        if (msg == null) return null;

        return SupportMessageDto.builder()
                .id(msg.getId())
                .ticketId(msg.getTicket().getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getName())
                .senderRole(msg.getSender().getRoles().stream().findFirst().map(r -> r.getName().name()).orElse("USER"))
                .message(msg.getMessage())
                .createdAt(msg.getCreatedAt())
                .isMe(currentUserId != null && msg.getSender().getId().equals(currentUserId))
                .build();
    }
}
