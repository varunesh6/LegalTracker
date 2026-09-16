package com.legaltrack.service;

import com.legaltrack.dto.chat.ConversationDto;
import com.legaltrack.dto.chat.MessageDto;
import com.legaltrack.dto.chat.SendMessageRequest;

import java.util.List;

public interface ChatService {
    List<ConversationDto> getMyConversations();
    ConversationDto getOrCreateConversation(Long lawyerId, Long caseId);
    List<MessageDto> getConversationMessages(Long conversationId);
    MessageDto sendMessage(Long conversationId, SendMessageRequest request);
    void markConversationAsRead(Long conversationId);
}
