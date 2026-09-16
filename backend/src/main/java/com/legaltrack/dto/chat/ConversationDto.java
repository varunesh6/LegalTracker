package com.legaltrack.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {

    private Long id;
    private Long clientId;
    private String clientName;
    private Long lawyerId;
    private String lawyerName;
    private Long caseId;
    private String caseTitle;
    private String caseNumber;
    private MessageDto lastMessage;
    private Long unreadCount;
    private LocalDateTime createdAt;
}
