package com.legaltrack.service;

import com.legaltrack.dto.casefile.CaseDiaryEntryDto;
import com.legaltrack.dto.casefile.CreateDiaryEntryRequest;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.User;
import com.legaltrack.enums.DiaryEntryType;
import com.legaltrack.enums.DiaryVisibility;

import java.time.LocalDateTime;
import java.util.List;

public interface CaseDiaryService {
    CaseDiaryEntryDto addDiaryEntry(Long caseId, CreateDiaryEntryRequest request);
    void recordAutomaticDiaryEntry(CaseFile caseFile, User actor, DiaryEntryType type, String title, String description, LocalDateTime eventDate, DiaryVisibility visibility);
    List<CaseDiaryEntryDto> getDiaryEntriesForCase(Long caseId);
}
