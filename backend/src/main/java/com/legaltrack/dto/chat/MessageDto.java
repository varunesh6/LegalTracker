package com.legaltrack.dto.chat;

import com.legaltrack.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private MessageType messageType;
    private String content;
    private Long attachmentId;
    private String attachmentName;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private boolean isMe;
}
