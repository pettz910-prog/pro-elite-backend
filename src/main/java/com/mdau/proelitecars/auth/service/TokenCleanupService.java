package com.mdau.proelitecars.auth.service;

import com.mdau.proelitecars.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    // ── Runs every day at 2:00 AM ─────────────────────────────────────────
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void purgeExpiredAndRevokedTokens() {
        log.info("🧹 Starting refresh token cleanup...");
        int deleted = refreshTokenRepository.deleteExpiredAndRevoked(Instant.now());
        log.info("✅ Token cleanup complete — {} tokens removed", deleted);
    }
}