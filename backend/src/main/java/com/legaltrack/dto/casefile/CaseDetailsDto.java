package com.legaltrack.dto.casefile;

import com.legaltrack.dto.document.CaseDocumentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetailsDto {

    private CaseFileDto caseFile;
    private List<CasePartyDto> parties;
    private List<CaseAdvocateDto> advocates;
    private List<CaseHearingDto> hearings;
    private List<CaseOrderDto> orders;
    private List<CaseDocumentDto> documents;
    private List<CaseDiaryEntryDto> diaryEntries;
    private List<CaseAttentionDto> attentionItems;
    private List<CaseNoteDto> notes;
    private Boolean isTrackedByCurrentUser;
    private Boolean isNotificationsEnabled;
    private String userNickname;
}
