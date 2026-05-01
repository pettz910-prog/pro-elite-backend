package com.mdau.proelitecars.auth.service;

import com.mdau.proelitecars.auth.repository.RefreshTokenRepository;
import com.mdau.proelitecars.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBreachService {

    private final RefreshTokenRepository refreshTokenRepository;

    // ── Runs in its OWN transaction — commits immediately ─────────────────
    // This ensures the delete is committed before the parent transaction
    // throws BusinessException (which would otherwise roll everything back).
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void wipeAllTokensForUser(User user) {
        refreshTokenRepository.deleteAllTokensByUser(user);
        log.warn("❌ All refresh tokens wiped for breached user: {}",
                user.getEmail());
    }
}