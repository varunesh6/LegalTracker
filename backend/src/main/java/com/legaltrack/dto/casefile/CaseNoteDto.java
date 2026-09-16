package com.legaltrack.dto.casefile;

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
public class CaseNoteDto {
    private Long id;
    private Long caseId;
    private Long userId;
    private String userName;
    private String title;
    private String content;
    private DiaryVisibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
