package com.legaltrack.service;

import com.legaltrack.dto.casefile.CaseAttentionDto;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.enums.Severity;

import java.util.List;

public interface CaseAttentionService {
    void createAttention(CaseFile caseFile, String type, String title, String description, Severity severity, String actionUrl);
    void evaluateAttentionRulesForCase(CaseFile caseFile);
    List<CaseAttentionDto> getAttentionForCase(Long caseId);
    List<CaseAttentionDto> getUnresolvedAttentionForCurrentUser();
    void resolveAttention(Long attentionId);
}
