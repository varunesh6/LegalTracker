package com.legaltrack.dto.casefile;

import com.legaltrack.enums.DiaryEntryType;
import com.legaltrack.enums.DiaryVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDiaryEntryDto {
    private Long id;
    private Long caseId;
    private Long createdById;
    private String createdByName;
    private String createdByRole;
    private DiaryEntryType entryType;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private DiaryVisibility visibility;
    private LocalDateTime createdAt;
}
