package com.legaltrack.controller;

import com.legaltrack.dto.common.ApiResponse;
import com.legaltrack.dto.document.CaseDocumentDto;
import com.legaltrack.enums.DocumentCategory;
import com.legaltrack.enums.DocumentVisibility;
import com.legaltrack.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document Controller", description = "Endpoints for case document upload, download, and version management")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/cases/{caseId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER', 'ADMIN')")
    @Operation(summary = "Upload a document for a case with category and visibility")
    public ResponseEntity<ApiResponse<CaseDocumentDto>> uploadDocument(
            @PathVariable Long caseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") DocumentCategory category,
            @RequestParam(value = "visibility", defaultValue = "LAWYER_AND_CLIENT") DocumentVisibility visibility,
            @RequestParam(value = "parentDocumentId", required = false) Long parentDocumentId) {
        
        CaseDocumentDto uploadedDoc = documentService.uploadCaseDocument(caseId, file, category, visibility, parentDocumentId);
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", uploadedDoc));
    }

    @GetMapping("/cases/{caseId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER', 'LEGAL_AID_OFFICER', 'ADMIN')")
    @Operation(summary = "Get list of accessible documents for a case")
    public ResponseEntity<ApiResponse<List<CaseDocumentDto>>> getCaseDocuments(@PathVariable Long caseId) {
        List<CaseDocumentDto> docs = documentService.getDocumentsForCase(caseId);
        return ResponseEntity.ok(ApiResponse.success(docs));
    }

    @GetMapping("/{documentId}/download")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER', 'LEGAL_AID_OFFICER', 'ADMIN')")
    @Operation(summary = "Download a document by ID with proper access control")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        Resource resource = documentService.downloadDocument(documentId);
        String filename = resource.getFilename() != null ? resource.getFilename() : "document";
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{documentId}/versions")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER', 'ADMIN')")
    @Operation(summary = "Get version history of a document")
    public ResponseEntity<ApiResponse<List<CaseDocumentDto>>> getDocumentVersionHistory(@PathVariable Long documentId) {
        List<CaseDocumentDto> versions = documentService.getDocumentVersionHistory(documentId);
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    @Operation(summary = "Delete a document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }
}
