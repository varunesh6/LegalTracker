package com.legaltrack.dto.chat;

import com.legaltrack.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotBlank(message = "Message content is required")
    private String content;

    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    private Long attachmentId;
}
