package com.legaltrack.scheduler;

import com.legaltrack.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired refresh tokens...");
        int deleted = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        log.info("Cleaned up {} expired refresh tokens.", deleted);
    }
}
