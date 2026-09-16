package com.legaltrack.service;

import com.legaltrack.dto.casefile.CaseDetailsDto;
import com.legaltrack.dto.casefile.CaseFileDto;
import com.legaltrack.dto.casefile.CaseSearchCriteria;
import com.legaltrack.dto.casefile.TrackByCnrRequest;
import com.legaltrack.dto.common.PagedResponse;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.Court;
import com.legaltrack.entity.User;
import com.legaltrack.enums.CaseStage;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.integration.court.CourtDataProvider;
import com.legaltrack.mapper.CaseMapper;
import com.legaltrack.mapper.DocumentMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.security.UserPrincipal;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.CaseAttentionService;
import com.legaltrack.service.CaseDiaryService;
import com.legaltrack.service.impl.CaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaseServiceTest {

    @Mock
    private CaseFileRepository caseFileRepository;

    @Mock
    private CasePartyRepository partyRepository;

    @Mock
    private CaseAdvocateRepository advocateRepository;

    @Mock
    private CaseEventRepository eventRepository;

    @Mock
    private CaseNoteRepository noteRepository;

    @Mock
    private CaseDocumentRepository documentRepository;

    @Mock
    private TrackedCaseRepository trackedCaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private CourtComplexRepository courtComplexRepository;

    @Mock
    private PoliceStationRepository policeStationRepository;

    @Mock
    private CourtDataProvider mockCourtDataProvider;

    @Mock
    private CaseMapper caseMapper;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private CaseDiaryService caseDiaryService;

    @Mock
    private CaseAttentionService caseAttentionService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CaseServiceImpl caseService;

    private CaseFile testCase;
    private CaseFileDto testCaseFileDto;

    @BeforeEach
    void setUp() {
        User client = User.builder().id(10L).name("Test Client").build();
        UserPrincipal principal = UserPrincipal.create(client);
        org.springframework.security.core.context.SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);

        Court court = Court.builder().id(1L).name("Principal District Court").build();
        testCase = CaseFile.builder()
                .id(50L)
                .cnrNumber("TNCH010012342024")
                .caseNumber("OS/102/2024")
                .filingNumber("FIL/2024/0089")
                .filingDate(LocalDate.of(2024, 1, 15))
                .status(CaseStatus.PENDING)
                .stage(CaseStage.ARGUMENTS)
                .client(client)
                .court(court)
                .hearings(new ArrayList<>())
                .orders(new ArrayList<>())
                .parties(new ArrayList<>())
                .advocates(new ArrayList<>())
                .build();

        testCaseFileDto = CaseFileDto.builder()
                .id(50L)
                .cnrNumber("TNCH010012342024")
                .caseNumber("OS/102/2024")
                .status(CaseStatus.PENDING)
                .stage(CaseStage.ARGUMENTS)
                .courtName("Principal District Court")
                .build();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should find case by valid CNR number via trackByCnr")
    void testTrackByCnr() {
        TrackByCnrRequest request = new TrackByCnrRequest();
        request.setCnrNumber("TNCH010012342024");

        when(mockCourtDataProvider.searchByCnr("TNCH010012342024")).thenReturn(Optional.of(testCaseFileDto));
        when(caseFileRepository.findByCnrNumber("TNCH010012342024")).thenReturn(Optional.of(testCase));
        when(caseFileRepository.findById(50L)).thenReturn(Optional.of(testCase));
        when(caseMapper.toDto(testCase)).thenReturn(testCaseFileDto);

        CaseDetailsDto result = caseService.trackByCnr(request);

        assertNotNull(result);
        assertNotNull(result.getCaseFile());
        assertEquals("TNCH010012342024", result.getCaseFile().getCnrNumber());
        assertEquals(CaseStatus.PENDING, result.getCaseFile().getStatus());
    }

    @Test
    @DisplayName("Should search cases with criteria")
    void testSearchCases() {
        CaseSearchCriteria criteria = new CaseSearchCriteria();
        criteria.setQuery("OS/102");
        criteria.setSortBy("createdAt");
        criteria.setSortDirection("desc");
        criteria.setPage(0);
        criteria.setSize(10);
        Page<CaseFile> page = new PageImpl<>(List.of(testCase));

        when(caseFileRepository.searchCases(isNull(), isNull(), isNull(), eq("OS/102"), any(Pageable.class))).thenReturn(page);
        when(caseMapper.toDto(testCase)).thenReturn(testCaseFileDto);

        PagedResponse<CaseFileDto> response = caseService.searchCases(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("OS/102/2024", response.getContent().get(0).getCaseNumber());
    }
}
