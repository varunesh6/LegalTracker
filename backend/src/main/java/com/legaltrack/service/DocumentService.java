package com.legaltrack.service;

import com.legaltrack.dto.document.CaseDocumentDto;
import com.legaltrack.enums.DocumentCategory;
import com.legaltrack.enums.DocumentVisibility;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    CaseDocumentDto uploadCaseDocument(Long caseId, MultipartFile file, DocumentCategory category, DocumentVisibility visibility, Long parentDocumentId);
    Resource downloadDocument(Long documentId);
    List<CaseDocumentDto> getDocumentsForCase(Long caseId);
    List<CaseDocumentDto> getDocumentVersionHistory(Long parentDocumentId);
    void deleteDocument(Long documentId);
}
