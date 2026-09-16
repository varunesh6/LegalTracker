package com.legaltrack.service;

import com.legaltrack.dto.legalaid.LegalAidPreCheckRequest;
import com.legaltrack.dto.legalaid.LegalAidPreCheckResponse;
import com.legaltrack.enums.LegalAidCategory;
import com.legaltrack.mapper.LegalAidMapper;
import com.legaltrack.repository.*;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.NotificationService;
import com.legaltrack.service.impl.LegalAidServiceImpl;
import com.legaltrack.storage.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LegalAidServiceTest {

    @Mock
    private LegalAidApplicationRepository applicationRepository;

    @Mock
    private LegalAidDocumentRepository documentRepository;

    @Mock
    private LegalAidStatusHistoryRepository historyRepository;

    @Mock
    private LegalAidAssignmentRepository assignmentRepository;

    @Mock
    private LegalAidEligibilityRuleRepository ruleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private CaseFileRepository caseFileRepository;

    @Mock
    private TrackedCaseRepository trackedCaseRepository;

    @Mock
    private LegalAidMapper legalAidMapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private LegalAidServiceImpl legalAidService;

    @Test
    @DisplayName("Should evaluate eligibility as eligible for Woman/Child category")
    void testPreCheckEligibilityWomanOrChild() {
        LegalAidPreCheckRequest request = new LegalAidPreCheckRequest();
        request.setCategory(LegalAidCategory.WOMAN_OR_CHILD);
        request.setAnnualIncome(new BigDecimal("500000"));
        request.setReasonForAssistance("Custody battle");

        LegalAidPreCheckResponse response = legalAidService.preCheckEligibility(request);

        assertNotNull(response);
        assertTrue(response.isEligible());
        assertEquals("POTENTIALLY_ELIGIBLE", response.getStatus());
    }

    @Test
    @DisplayName("Should evaluate eligibility for low income under threshold")
    void testPreCheckEligibilityLowIncome() {
        LegalAidPreCheckRequest request = new LegalAidPreCheckRequest();
        request.setCategory(LegalAidCategory.LOW_INCOME_GENERAL);
        request.setAnnualIncome(new BigDecimal("150000"));

        LegalAidPreCheckResponse response = legalAidService.preCheckEligibility(request);

        assertNotNull(response);
        assertTrue(response.isEligible());
        assertEquals("POTENTIALLY_ELIGIBLE", response.getStatus());
    }
}
