package com.mdau.proelitecars.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    // ── Generate access token ─────────────────────────────────────────────
    public String generateAccessToken(UUID userId, String email, String role) {
        Date now        = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiryMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .notBefore(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    // ── Extract claims ────────────────────────────────────────────────────
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clockSkewSeconds(30)   // allow 30s clock drift
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ── Validate token ────────────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Reject tokens issued in the future
            if (claims.getIssuedAt() != null
                    && claims.getIssuedAt().after(new Date())) {
                log.warn("❌ JWT issued in the future — rejected");
                return false;
            }

            return true;

        } catch (ExpiredJwtException e) {
            log.warn("❌ JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("❌ JWT unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("❌ JWT malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("❌ JWT signature invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("❌ JWT empty/null: {}", e.getMessage());
        }
        return false;
    }

    // ── Signing key — expects Base64-encoded secret in config ─────────────
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}