package com.legaltrack.dto.legalaid;

import com.legaltrack.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAidDocumentDto {
    private Long id;
    private Long applicationId;
    private String documentName;
    private String documentType;
    private String storageKey;
    private String mimeType;
    private Long fileSize;
    private VerificationStatus verificationStatus;
    private String remarks;
    private LocalDateTime uploadedAt;
}
