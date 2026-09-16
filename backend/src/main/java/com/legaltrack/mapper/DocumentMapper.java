package com.legaltrack.mapper;

import com.legaltrack.dto.document.CaseDocumentDto;
import com.legaltrack.entity.CaseDocument;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class DocumentMapper {

    public CaseDocumentDto toDto(CaseDocument doc) {
        if (doc == null) return null;

        return CaseDocumentDto.builder()
                .id(doc.getId())
                .caseId(doc.getCaseFile().getId())
                .uploadedById(doc.getUploadedBy().getId())
                .uploadedByName(doc.getUploadedBy().getName())
                .uploadedByRole(doc.getUploadedBy().getRoles().stream().findFirst().map(r -> r.getName().name()).orElse("USER"))
                .category(doc.getCategory())
                .fileName(doc.getFileName())
                .mimeType(doc.getMimeType())
                .fileSize(doc.getFileSize())
                .formattedSize(formatFileSize(doc.getFileSize()))
                .checksum(doc.getChecksum())
                .visibility(doc.getVisibility())
                .version(doc.getVersion())
                .parentDocumentId(doc.getParentDocument() != null ? doc.getParentDocument().getId() : null)
                .uploadedAt(doc.getUploadedAt())
                .build();
    }

    private String formatFileSize(Long size) {
        if (size == null || size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
