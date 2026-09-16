package com.legaltrack.mapper;

import com.legaltrack.dto.legalaid.LegalAidApplicationDto;
import com.legaltrack.dto.legalaid.LegalAidDocumentDto;
import com.legaltrack.dto.legalaid.LegalAidStatusHistoryDto;
import com.legaltrack.entity.LegalAidApplication;
import com.legaltrack.entity.LegalAidDocument;
import com.legaltrack.entity.LegalAidStatusHistory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LegalAidMapper {

    public LegalAidApplicationDto toDto(LegalAidApplication app) {
        if (app == null) return null;

        return LegalAidApplicationDto.builder()
                .id(app.getId())
                .applicationNumber(app.getApplicationNumber())
                .clientId(app.getClient().getId())
                .clientEmail(app.getClient().getEmail())
                .courtId(app.getCourt() != null ? app.getCourt().getId() : null)
                .courtName(app.getCourt() != null ? app.getCourt().getName() : null)
                .fullName(app.getFullName())
                .dateOfBirth(app.getDateOfBirth())
                .gender(app.getGender())
                .phone(app.getPhone())
                .email(app.getEmail())
                .address(app.getAddress())
                .stateId(app.getState() != null ? app.getState().getId() : null)
                .stateName(app.getState() != null ? app.getState().getName() : null)
                .districtId(app.getDistrict() != null ? app.getDistrict().getId() : null)
                .districtName(app.getDistrict() != null ? app.getDistrict().getName() : null)
                .caseType(app.getCaseType())
                .caseStage(app.getCaseStage())
                .matterDescription(app.getMatterDescription())
                .opponentInformation(app.getOpponentInformation())
                .annualIncome(app.getAnnualIncome())
                .employmentStatus(app.getEmploymentStatus())
                .selectedCategory(app.getSelectedCategory())
                .supportingInformation(app.getSupportingInformation())
                .status(app.getStatus())
                .assignedLawyerId(app.getAssignedLawyer() != null ? app.getAssignedLawyer().getId() : null)
                .assignedLawyerName(app.getAssignedLawyer() != null ? app.getAssignedLawyer().getName() : null)
                .createdCaseId(app.getCreatedCase() != null ? app.getCreatedCase().getId() : null)
                .documents(app.getDocuments() != null ?
                        app.getDocuments().stream().map(this::toDocumentDto).collect(Collectors.toList()) :
                        Collections.emptyList())
                .statusHistory(app.getStatusHistory() != null ?
                        app.getStatusHistory().stream().map(this::toStatusHistoryDto).collect(Collectors.toList()) :
                        Collections.emptyList())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    public LegalAidDocumentDto toDocumentDto(LegalAidDocument doc) {
        if (doc == null) return null;
        return LegalAidDocumentDto.builder()
                .id(doc.getId())
                .applicationId(doc.getApplication().getId())
                .documentName(doc.getDocumentName())
                .documentType(doc.getDocumentType())
                .storageKey(doc.getStorageKey())
                .mimeType(doc.getMimeType())
                .fileSize(doc.getFileSize())
                .verificationStatus(doc.getVerificationStatus())
                .remarks(doc.getRemarks())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }

    public LegalAidStatusHistoryDto toStatusHistoryDto(LegalAidStatusHistory history) {
        if (history == null) return null;
        return LegalAidStatusHistoryDto.builder()
                .id(history.getId())
                .applicationId(history.getApplication().getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .remarks(history.getRemarks())
                .changedById(history.getChangedBy() != null ? history.getChangedBy().getId() : null)
                .changedByName(history.getChangedBy() != null ? history.getChangedBy().getName() : null)
                .changedAt(history.getChangedAt())
                .build();
    }
}
