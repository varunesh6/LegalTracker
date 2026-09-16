package com.legaltrack.mapper;

import com.legaltrack.dto.casefile.*;
import com.legaltrack.entity.*;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CaseMapper {

    public CaseFileDto toDto(CaseFile caseFile) {
        if (caseFile == null) return null;

        return CaseFileDto.builder()
                .id(caseFile.getId())
                .internalReferenceId(caseFile.getInternalReferenceId())
                .clientId(caseFile.getClient() != null ? caseFile.getClient().getId() : null)
                .clientName(caseFile.getClient() != null ? caseFile.getClient().getName() : null)
                .lawyerId(caseFile.getLawyer() != null ? caseFile.getLawyer().getId() : null)
                .lawyerName(caseFile.getLawyer() != null ? caseFile.getLawyer().getName() : null)
                .courtId(caseFile.getCourt() != null ? caseFile.getCourt().getId() : null)
                .courtName(caseFile.getCourt() != null ? caseFile.getCourt().getName() : null)
                .courtComplexName(caseFile.getCourtComplex() != null ? caseFile.getCourtComplex().getName() : null)
                .stateId(caseFile.getState() != null ? caseFile.getState().getId() : null)
                .stateName(caseFile.getState() != null ? caseFile.getState().getName() : null)
                .districtId(caseFile.getDistrict() != null ? caseFile.getDistrict().getId() : null)
                .districtName(caseFile.getDistrict() != null ? caseFile.getDistrict().getName() : null)
                .caseType(caseFile.getCaseType())
                .caseCategory(caseFile.getCaseCategory())
                .title(caseFile.getTitle())
                .cnrNumber(caseFile.getCnrNumber())
                .caseNumber(caseFile.getCaseNumber())
                .filingNumber(caseFile.getFilingNumber())
                .filingDate(caseFile.getFilingDate())
                .registrationNumber(caseFile.getRegistrationNumber())
                .registrationDate(caseFile.getRegistrationDate())
                .firNumber(caseFile.getFirNumber())
                .firYear(caseFile.getFirYear())
                .policeStationName(caseFile.getPoliceStation() != null ? caseFile.getPoliceStation().getName() : null)
                .act(caseFile.getAct())
                .section(caseFile.getSection())
                .status(caseFile.getStatus())
                .stage(caseFile.getStage())
                .matterDescription(caseFile.getMatterDescription())
                .engagementType(caseFile.getEngagementType())
                .isDemoData(caseFile.getIsDemoData())
                .nextHearingDate(caseFile.getNextHearingDate())
                .createdAt(caseFile.getCreatedAt())
                .updatedAt(caseFile.getUpdatedAt())
                .build();
    }

    public CasePartyDto toPartyDto(CaseParty party) {
        if (party == null) return null;
        return CasePartyDto.builder()
                .id(party.getId())
                .caseId(party.getCaseFile().getId())
                .name(party.getName())
                .partyType(party.getPartyType())
                .isPrimary(party.getIsPrimary())
                .contactInfo(party.getContactInfo())
                .createdAt(party.getCreatedAt())
                .build();
    }

    public CaseAdvocateDto toAdvocateDto(CaseAdvocate advocate) {
        if (advocate == null) return null;
        return CaseAdvocateDto.builder()
                .id(advocate.getId())
                .caseId(advocate.getCaseFile().getId())
                .advocateName(advocate.getAdvocateName())
                .registrationNumber(advocate.getRegistrationNumber())
                .partyRepresented(advocate.getPartyRepresented())
                .role(advocate.getRole())
                .createdAt(advocate.getCreatedAt())
                .build();
    }

    public CaseEventDto toEventDto(CaseEvent event) {
        if (event == null) return null;
        return CaseEventDto.builder()
                .id(event.getId())
                .caseId(event.getCaseFile().getId())
                .eventType(event.getEventType())
                .eventTitle(event.getEventTitle())
                .eventDescription(event.getEventDescription())
                .eventDate(event.getEventDate())
                .source(event.getSource())
                .createdById(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null)
                .createdByName(event.getCreatedBy() != null ? event.getCreatedBy().getName() : null)
                .createdAt(event.getCreatedAt())
                .build();
    }

    public CaseDiaryEntryDto toDiaryDto(CaseDiaryEntry entry) {
        if (entry == null) return null;
        return CaseDiaryEntryDto.builder()
                .id(entry.getId())
                .caseId(entry.getCaseFile().getId())
                .createdById(entry.getCreatedBy().getId())
                .createdByName(entry.getCreatedBy().getName())
                .createdByRole(entry.getCreatedBy().getRoles().stream().findFirst().map(r -> r.getName().name()).orElse("USER"))
                .entryType(entry.getEntryType())
                .title(entry.getTitle())
                .description(entry.getDescription())
                .eventDate(entry.getEventDate())
                .visibility(entry.getVisibility())
                .createdAt(entry.getCreatedAt())
                .build();
    }

    public CaseHearingDto toHearingDto(CaseHearing hearing) {
        if (hearing == null) return null;
        return CaseHearingDto.builder()
                .id(hearing.getId())
                .caseId(hearing.getCaseFile().getId())
                .caseTitle(hearing.getCaseFile().getTitle())
                .caseNumber(hearing.getCaseFile().getCaseNumber())
                .cnrNumber(hearing.getCaseFile().getCnrNumber())
                .hearingDate(hearing.getHearingDate())
                .hearingTime(hearing.getHearingTime())
                .courtId(hearing.getCourt() != null ? hearing.getCourt().getId() : null)
                .courtName(hearing.getCourt() != null ? hearing.getCourt().getName() : null)
                .judgeName(hearing.getJudgeName())
                .purpose(hearing.getPurpose())
                .stage(hearing.getStage())
                .status(hearing.getStatus())
                .notes(hearing.getNotes())
                .nextHearingDate(hearing.getNextHearingDate())
                .source(hearing.getSource())
                .createdAt(hearing.getCreatedAt())
                .build();
    }

    public CaseOrderDto toOrderDto(CaseOrder order) {
        if (order == null) return null;
        return CaseOrderDto.builder()
                .id(order.getId())
                .caseId(order.getCaseFile().getId())
                .orderDate(order.getOrderDate())
                .title(order.getTitle())
                .orderType(order.getOrderType())
                .documentId(order.getDocumentId())
                .summary(order.getSummary())
                .source(order.getSource())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public CaseNoteDto toNoteDto(CaseNote note) {
        if (note == null) return null;
        return CaseNoteDto.builder()
                .id(note.getId())
                .caseId(note.getCaseFile().getId())
                .userId(note.getUser().getId())
                .userName(note.getUser().getName())
                .title(note.getTitle())
                .content(note.getContent())
                .visibility(note.getVisibility())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    public CaseAttentionDto toAttentionDto(CaseAttention attention) {
        if (attention == null) return null;
        return CaseAttentionDto.builder()
                .id(attention.getId())
                .caseId(attention.getCaseFile().getId())
                .caseTitle(attention.getCaseFile().getTitle())
                .caseNumber(attention.getCaseFile().getCaseNumber())
                .cnrNumber(attention.getCaseFile().getCnrNumber())
                .type(attention.getType())
                .title(attention.getTitle())
                .description(attention.getDescription())
                .severity(attention.getSeverity())
                .actionUrl(attention.getActionUrl())
                .resolved(attention.getResolved())
                .resolvedAt(attention.getResolvedAt())
                .createdAt(attention.getCreatedAt())
                .build();
    }

    public CaseTimelineResponse toTimelineResponse(CaseFile caseFile, List<CaseEvent> events) {
        List<CaseEventDto> eventDtos = events.stream().map(this::toEventDto).collect(Collectors.toList());

        String[] standardStages = {
            "Filing", "Registration", "Summons / Notice", "Appearance", "Pleadings", "Issues", "Evidence", "Arguments", "Judgment"
        };

        String currentStageName = caseFile.getStage() != null ? caseFile.getStage().name() : "APPEARANCE";

        List<CaseTimelineResponse.StageProgressDto> stageProgressions = new ArrayList<>();
        boolean foundCurrent = false;

        for (String stg : standardStages) {
            boolean isCurrent = stg.equalsIgnoreCase(currentStageName) || currentStageName.contains(stg.toUpperCase());
            if (isCurrent) foundCurrent = true;

            stageProgressions.add(CaseTimelineResponse.StageProgressDto.builder()
                    .stageName(stg)
                    .current(isCurrent)
                    .completed(!foundCurrent || isCurrent)
                    .date(caseFile.getCreatedAt() != null ? caseFile.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : null)
                    .build());
        }

        return CaseTimelineResponse.builder()
                .caseId(caseFile.getId())
                .caseTitle(caseFile.getTitle())
                .cnrNumber(caseFile.getCnrNumber())
                .caseNumber(caseFile.getCaseNumber())
                .currentStage(currentStageName)
                .currentStatus(caseFile.getStatus().name())
                .events(eventDtos)
                .stageProgression(stageProgressions)
                .build();
    }
}
