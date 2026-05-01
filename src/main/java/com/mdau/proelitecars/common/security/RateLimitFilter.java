package com.mdau.proelitecars.common.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // ── Per-IP bucket cache ───────────────────────────────────────────────
    private final Map<String, Bucket> authBuckets    = new ConcurrentHashMap<>();
    private final Map<String, Bucket> inquiryBuckets = new ConcurrentHashMap<>();

    // ── Bucket factories ───────────────────────────────────────────────────
    private Bucket newAuthBucket() {
        // 10 requests per minute per IP for auth endpoints
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillGreedy(10, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    private Bucket newInquiryBucket() {
        // 20 requests per minute per IP for inquiry submission
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(20)
                        .refillGreedy(20, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip   = getClientIp(request);

        // ── Auth rate limiting ────────────────────────────────────────────
        if (path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/signup")) {

            Bucket bucket = authBuckets.computeIfAbsent(ip,
                    k -> newAuthBucket());

            if (!bucket.tryConsume(1)) {
                log.warn("❌ Rate limit exceeded on {} from IP: {}", path, ip);
                writeTooManyRequests(response,
                        "Too many authentication attempts. Please try again in a minute.");
                return;
            }
        }

        // ── Inquiry rate limiting ─────────────────────────────────────────
        if (path.equals("/api/inquiries")
                && "POST".equalsIgnoreCase(request.getMethod())) {

            Bucket bucket = inquiryBuckets.computeIfAbsent(ip,
                    k -> newInquiryBucket());

            if (!bucket.tryConsume(1)) {
                log.warn("❌ Rate limit exceeded on inquiry from IP: {}", ip);
                writeTooManyRequests(response,
                        "Too many inquiry submissions. Please try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // ── Extract real client IP (handles proxies) ──────────────────────────
    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletResponse response,
                                       String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"success\":false,\"message\":\"" + message + "\","
                + "\"status\":429}"
        );
    }
}