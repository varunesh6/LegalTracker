package com.legaltrack.dto.casefile;

import com.legaltrack.enums.EventSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseEventDto {
    private Long id;
    private Long caseId;
    private String eventType;
    private String eventTitle;
    private String eventDescription;
    private LocalDateTime eventDate;
    private EventSource source;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
