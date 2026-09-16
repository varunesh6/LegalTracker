package com.legaltrack.dto.document;

import com.legaltrack.enums.DocumentCategory;
import com.legaltrack.enums.DocumentVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDocumentDto {

    private Long id;
    private Long caseId;
    private Long uploadedById;
    private String uploadedByName;
    private String uploadedByRole;
    private DocumentCategory category;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String formattedSize;
    private String checksum;
    private DocumentVisibility visibility;
    private Integer version;
    private Long parentDocumentId;
    private LocalDateTime uploadedAt;
}
