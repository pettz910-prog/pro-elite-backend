package com.mdau.proelitecars.config;

import com.mdau.proelitecars.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${app.version:1.0.0}")
    private String version;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.info("✅ Health check requested");
        Map<String, Object> status = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "version", version
        );
        return ResponseEntity.ok(ApiResponse.ok("Service is healthy", status));
    }
}