package com.mdau.proelitecars.auth.service;

import com.mdau.proelitecars.auth.dto.AuthResponse;
import com.mdau.proelitecars.auth.dto.AuthUserDto;
import com.mdau.proelitecars.auth.dto.LoginRequest;
import com.mdau.proelitecars.auth.dto.RefreshTokenRequest;
import com.mdau.proelitecars.auth.dto.SignupRequest;
import com.mdau.proelitecars.auth.entity.RefreshToken;
import com.mdau.proelitecars.auth.repository.RefreshTokenRepository;
import com.mdau.proelitecars.common.exception.BusinessException;
import com.mdau.proelitecars.common.exception.ResourceNotFoundException;
import com.mdau.proelitecars.common.security.JwtUtil;
import com.mdau.proelitecars.user.entity.Role;
import com.mdau.proelitecars.user.entity.User;
import com.mdau.proelitecars.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBreachService tokenBreachService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", request.getEmail()));
        String accessToken  = jwtUtil.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = createRefreshToken(user);
        log.info("✅ User logged in: {} [{}]", user.getEmail(), user.getRole());
        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userRepository.save(user);
        String accessToken  = jwtUtil.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = createRefreshToken(user);
        log.info("✅ New user registered: {} [{}]",
                user.getEmail(), user.getRole());
        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(
                        "Invalid refresh token"));

        // ── Breach detection ───────────────────────────────────────────────
        // stored.isRevoked() = true means this token was already used.
        // Wipe all tokens in a SEPARATE committed transaction first,
        // then throw — the wipe will NOT be rolled back.
        if (stored.isRevoked()) {
            log.warn("❌ Breach detected for user: {}",
                    stored.getUser().getEmail());
            tokenBreachService.wipeAllTokensForUser(stored.getUser());
            throw new BusinessException(
                    "Refresh token reuse detected. Please log in again.");
        }

        if (stored.isExpired()) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new BusinessException(
                    "Refresh token expired. Please log in again.");
        }

        // ── Rotate ────────────────────────────────────────────────────────
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        String newAccessToken  = jwtUtil.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = createRefreshToken(user);

        log.info("✅ Tokens rotated for user: {}", user.getEmail());
        return buildAuthResponse(newAccessToken, newRefreshToken, user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("✅ User logged out: {}",
                            token.getUser().getEmail());
                });
    }

    @Transactional(readOnly = true)
    public AuthUserDto getMe(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", userId));
        return toAuthUserDto(user);
    }

    private String createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiryMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    private AuthResponse buildAuthResponse(String accessToken,
                                            String refreshToken,
                                            User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toAuthUserDto(user))
                .build();
    }

    private AuthUserDto toAuthUserDto(User user) {
        return AuthUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .build();
    }
}