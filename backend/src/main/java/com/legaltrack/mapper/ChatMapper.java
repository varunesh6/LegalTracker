package com.legaltrack.mapper;

import com.legaltrack.dto.chat.ConversationDto;
import com.legaltrack.dto.chat.MessageDto;
import com.legaltrack.entity.Conversation;
import com.legaltrack.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ConversationDto toConversationDto(Conversation conv, Message lastMessage, Long unreadCount) {
        if (conv == null) return null;

        return ConversationDto.builder()
                .id(conv.getId())
                .clientId(conv.getClient().getId())
                .clientName(conv.getClient().getName())
                .lawyerId(conv.getLawyer().getId())
                .lawyerName(conv.getLawyer().getName())
                .caseId(conv.getCaseFile() != null ? conv.getCaseFile().getId() : null)
                .caseTitle(conv.getCaseFile() != null ? conv.getCaseFile().getTitle() : null)
                .caseNumber(conv.getCaseFile() != null ? conv.getCaseFile().getCaseNumber() : null)
                .lastMessage(lastMessage != null ? toMessageDto(lastMessage, null) : null)
                .unreadCount(unreadCount != null ? unreadCount : 0L)
                .createdAt(conv.getCreatedAt())
                .build();
    }

    public MessageDto toMessageDto(Message msg, Long currentUserId) {
        if (msg == null) return null;

        return MessageDto.builder()
                .id(msg.getId())
                .conversationId(msg.getConversation().getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getName())
                .messageType(msg.getMessageType())
                .content(msg.getContent())
                .attachmentId(msg.getAttachment() != null ? msg.getAttachment().getId() : null)
                .attachmentName(msg.getAttachment() != null ? msg.getAttachment().getFileName() : null)
                .sentAt(msg.getSentAt())
                .readAt(msg.getReadAt())
                .isMe(currentUserId != null && msg.getSender().getId().equals(currentUserId))
                .build();
    }
}
