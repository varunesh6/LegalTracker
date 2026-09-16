package com.legaltrack.service.impl;

import com.legaltrack.dto.document.CaseDocumentDto;
import com.legaltrack.entity.CaseDocument;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.User;
import com.legaltrack.enums.DiaryEntryType;
import com.legaltrack.enums.DiaryVisibility;
import com.legaltrack.enums.DocumentCategory;
import com.legaltrack.enums.DocumentVisibility;
import com.legaltrack.enums.NotificationType;
import com.legaltrack.exception.ResourceNotFoundException;
import com.legaltrack.exception.UnauthorizedAccessException;
import com.legaltrack.mapper.DocumentMapper;
import com.legaltrack.repository.CaseDocumentRepository;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.DocumentService;
import com.legaltrack.service.NotificationService;
import com.legaltrack.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final CaseDocumentRepository documentRepository;
    private final CaseFileRepository caseFileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final DocumentMapper documentMapper;
    private final CaseDiaryService caseDiaryService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CaseDocumentDto uploadCaseDocument(Long caseId, MultipartFile file, DocumentCategory category, DocumentVisibility visibility, Long parentDocumentId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        validateCaseAccess(caseFile, currentUserId);

        String storageKey = fileStorageService.storeFile(file, "cases");
        String checksum = fileStorageService.calculateChecksum(file);

        int version = 1;
        CaseDocument parentDoc = null;

        if (parentDocumentId != null) {
            parentDoc = documentRepository.findById(parentDocumentId).orElse(null);
            if (parentDoc != null) {
                version = parentDoc.getVersion() + 1;
            }
        }

        CaseDocument document = CaseDocument.builder()
                .caseFile(caseFile)
                .uploadedBy(currentUser)
                .category(category != null ? category : DocumentCategory.CLIENT_DOCUMENT)
                .fileName(file.getOriginalFilename())
                .storageKey(storageKey)
                .mimeType(file.getContentType() != null ? file.getContentType() : "application/pdf")
                .fileSize(file.getSize())
                .checksum(checksum)
                .visibility(visibility != null ? visibility : DocumentVisibility.CLIENT_AND_LAWYER)
                .version(version)
                .parentDocument(parentDoc)
                .build();

        CaseDocument saved = documentRepository.save(document);

        // Record automated case diary entry
        caseDiaryService.recordAutomaticDiaryEntry(
                caseFile,
                currentUser,
                DiaryEntryType.DOCUMENT_UPLOADED,
                "Document Uploaded: " + file.getOriginalFilename() + " (v" + version + ")",
                "Category: " + document.getCategory().name(),
                LocalDateTime.now(),
                document.getVisibility() == DocumentVisibility.CLIENT_AND_LAWYER ? DiaryVisibility.SHARED : DiaryVisibility.CLIENT_PRIVATE
        );

        // Notify other party
        if (document.getVisibility() == DocumentVisibility.CLIENT_AND_LAWYER) {
            if (caseFile.getClient() != null && !caseFile.getClient().getId().equals(currentUserId)) {
                notificationService.createNotification(
                        caseFile.getClient(),
                        NotificationType.DOCUMENT_UPLOADED,
                        "New Document Uploaded",
                        currentUser.getName() + " uploaded " + file.getOriginalFilename() + " for " + caseFile.getTitle(),
                        "DOCUMENT",
                        saved.getId(),
                        "/client/cases/" + caseFile.getId() + "/documents"
                );
            }
            if (caseFile.getLawyer() != null && !caseFile.getLawyer().getId().equals(currentUserId)) {
                notificationService.createNotification(
                        caseFile.getLawyer(),
                        NotificationType.DOCUMENT_UPLOADED,
                        "New Document Uploaded",
                        currentUser.getName() + " uploaded " + file.getOriginalFilename() + " for " + caseFile.getTitle(),
                        "DOCUMENT",
                        saved.getId(),
                        "/lawyer/cases/" + caseFile.getId() + "/documents"
                );
            }
        }

        auditLogService.logAction(currentUser, "DOCUMENT_UPLOAD", "CaseDocument", saved.getId(), null, "Uploaded document " + file.getOriginalFilename() + " to case " + caseFile.getId());

        return documentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CaseDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("CaseDocument", "id", documentId));

        validateDocumentAccess(doc, currentUserId);

        User currentUser = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;
        auditLogService.logAction(currentUser, "DOCUMENT_DOWNLOAD", "CaseDocument", documentId, null, "Downloaded document " + doc.getFileName());

        return fileStorageService.loadFileAsResource(doc.getStorageKey());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseDocumentDto> getDocumentsForCase(Long caseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return documentRepository.findAuthorizedDocuments(caseId, currentUserId != null ? currentUserId : -1L, SecurityUtils.isAdmin()).stream()
                .map(documentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseDocumentDto> getDocumentVersionHistory(Long parentDocumentId) {
        return documentRepository.findByParentDocumentIdOrderByVersionAsc(parentDocumentId).stream()
                .map(documentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CaseDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("CaseDocument", "id", documentId));

        if (!doc.getUploadedBy().getId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedAccessException("Cannot delete document uploaded by another user");
        }

        fileStorageService.deleteFile(doc.getStorageKey());
        documentRepository.delete(doc);

        auditLogService.logAction(doc.getUploadedBy(), "DOCUMENT_DELETE", "CaseDocument", documentId, null, "Deleted document " + doc.getFileName());
    }

    private void validateCaseAccess(CaseFile caseFile, Long userId) {
        if (SecurityUtils.isAdmin()) return;
        boolean isClient = caseFile.getClient() != null && caseFile.getClient().getId().equals(userId);
        boolean isLawyer = caseFile.getLawyer() != null && caseFile.getLawyer().getId().equals(userId);
        if (!isClient && !isLawyer) {
            throw new UnauthorizedAccessException("You are not authorized to access documents for this case");
        }
    }

    private void validateDocumentAccess(CaseDocument doc, Long userId) {
        if (SecurityUtils.isAdmin()) return;
        if (doc.getVisibility() == DocumentVisibility.PUBLIC_COURT_RECORD) return;
        if (doc.getVisibility() == DocumentVisibility.CLIENT_AND_LAWYER) {
            validateCaseAccess(doc.getCaseFile(), userId);
            return;
        }
        if (doc.getUploadedBy().getId().equals(userId)) return;

        throw new UnauthorizedAccessException("Access denied to private document");
    }
}
