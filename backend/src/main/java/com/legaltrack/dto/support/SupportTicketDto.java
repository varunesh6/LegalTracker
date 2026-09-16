package com.legaltrack.dto.support;

import com.legaltrack.enums.SupportCategory;
import com.legaltrack.enums.SupportPriority;
import com.legaltrack.enums.SupportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketDto {

    private Long id;
    private String ticketNumber;
    private Long userId;
    private String userName;
    private String userEmail;
    private SupportCategory category;
    private String subject;
    private String description;
    private SupportPriority priority;
    private SupportStatus status;
    private List<SupportMessageDto> messages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
