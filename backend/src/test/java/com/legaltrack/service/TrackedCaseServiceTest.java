package com.legaltrack.service;

import com.legaltrack.dto.tracked.TrackCaseRequest;
import com.legaltrack.dto.tracked.TrackedCaseDto;
import com.legaltrack.entity.CaseFile;
import com.legaltrack.entity.TrackedCase;
import com.legaltrack.entity.User;
import com.legaltrack.enums.CaseStatus;
import com.legaltrack.mapper.TrackedCaseMapper;
import com.legaltrack.repository.CaseFileRepository;
import com.legaltrack.repository.TrackedCaseRepository;
import com.legaltrack.repository.UserRepository;
import com.legaltrack.security.SecurityUtils;
import com.legaltrack.security.UserPrincipal;
import com.legaltrack.service.AuditLogService;
import com.legaltrack.service.impl.TrackedCaseServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrackedCaseServiceTest {

    @Mock
    private TrackedCaseRepository trackedCaseRepository;

    @Mock
    private CaseFileRepository caseFileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrackedCaseMapper trackedCaseMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TrackedCaseServiceImpl trackedCaseService;

    private User testUser;
    private CaseFile testCase;
    private TrackedCase testTrackedCase;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(10L).name("Test Client").email("client@test.com").build();
        testCase = CaseFile.builder().id(20L).cnrNumber("TNCH010012342024").caseNumber("OS/102/2024").status(CaseStatus.PENDING).build();
        testTrackedCase = TrackedCase.builder()
                .id(1L)
                .user(testUser)
                .caseFile(testCase)
                .nickname("My Property Suit")
                .notificationsEnabled(true)
                .build();

        UserPrincipal principal = UserPrincipal.create(testUser);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should successfully track a new case")
    void testTrackCaseSuccess() {
        TrackCaseRequest request = new TrackCaseRequest();
        request.setCaseId(20L);
        request.setNickname("My Property Suit");
        request.setNotificationsEnabled(true);

        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(caseFileRepository.findById(20L)).thenReturn(Optional.of(testCase));
        when(trackedCaseRepository.existsByUserIdAndCaseFileId(10L, 20L)).thenReturn(false);
        when(trackedCaseRepository.save(any(TrackedCase.class))).thenReturn(testTrackedCase);
        when(trackedCaseMapper.toDto(any(TrackedCase.class))).thenReturn(TrackedCaseDto.builder()
                .id(1L)
                .caseId(20L)
                .nickname("My Property Suit")
                .build());

        TrackedCaseDto result = trackedCaseService.trackCase(request);

        assertNotNull(result);
        assertEquals("My Property Suit", result.getNickname());
        verify(trackedCaseRepository).save(any(TrackedCase.class));
    }

    @Test
    @DisplayName("Should stop tracking an existing case")
    void testStopTracking() {
        when(trackedCaseRepository.findById(1L)).thenReturn(Optional.of(testTrackedCase));

        trackedCaseService.stopTracking(1L);

        verify(trackedCaseRepository).delete(testTrackedCase);
    }
}
