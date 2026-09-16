package com.legaltrack.controller;

import com.legaltrack.dto.chat.ConversationDto;
import com.legaltrack.dto.chat.MessageDto;
import com.legaltrack.dto.chat.SendMessageRequest;
import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Controller", description = "Endpoints for 1-to-1 client-lawyer real-time and REST messaging")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER')")
    @Operation(summary = "Get all active conversations for the authenticated user")
    public ResponseEntity<ApiResponse<List<ConversationDto>>> getMyConversations() {
        List<ConversationDto> conversations = chatService.getMyConversations();
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }

    @PostMapping("/conversations")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER')")
    @Operation(summary = "Get or create a conversation between client and lawyer")
    public ResponseEntity<ApiResponse<ConversationDto>> getOrCreateConversation(
            @RequestParam Long lawyerId,
            @RequestParam(required = false) Long caseId) {
        ConversationDto conv = chatService.getOrCreateConversation(lawyerId, caseId);
        return ResponseEntity.ok(ApiResponse.success(conv));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER')")
    @Operation(summary = "Get message history for a conversation")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getConversationMessages(@PathVariable Long conversationId) {
        List<MessageDto> messages = chatService.getConversationMessages(conversationId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER')")
    @Operation(summary = "Send a message in a conversation")
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request) {
        MessageDto message = chatService.sendMessage(conversationId, request);
        return ResponseEntity.ok(ApiResponse.success("Message sent", message));
    }

    @PutMapping("/conversations/{conversationId}/read")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER')")
    @Operation(summary = "Mark all unread messages in a conversation as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long conversationId) {
        chatService.markConversationAsRead(conversationId);
        return ResponseEntity.ok(ApiResponse.success("Conversation marked as read", null));
    }

    // STOMP WebSocket Message Mapping
    @MessageMapping("/chat/{conversationId}")
    public MessageDto handleWebSocketMessage(
            @DestinationVariable Long conversationId,
            @Payload SendMessageRequest request) {
        return chatService.sendMessage(conversationId, request);
    }
}
