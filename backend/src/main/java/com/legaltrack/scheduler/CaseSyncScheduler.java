package com.legaltrack.scheduler;

import com.legaltrack.integration.court.CaseSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaseSyncScheduler {

    private final CaseSyncService caseSyncService;

    @Scheduled(fixedRateString = "${case.sync.fixed-rate:3600000}", initialDelay = 60000)
    public void runCaseSync() {
        log.info("Executing scheduled court synchronization job...");
        caseSyncService.synchronizeAllTrackedCases();
    }
}
