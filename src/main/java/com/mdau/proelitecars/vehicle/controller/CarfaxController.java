package com.mdau.proelitecars.vehicle.controller;

import com.mdau.proelitecars.common.response.ApiResponse;
import com.mdau.proelitecars.vehicle.dto.CarfaxFullDto;
import com.mdau.proelitecars.vehicle.dto.CarfaxSummaryDto;
import com.mdau.proelitecars.vehicle.service.CarfaxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class CarfaxController {

    private final CarfaxService carfaxService;

    // ── GET /api/vehicles/{id}/carfax/summary — public ────────────────────
    @GetMapping("/{id}/carfax/summary")
    public ResponseEntity<ApiResponse<CarfaxSummaryDto>> summary(
            @PathVariable UUID id) {
        log.info("✅ CarFax summary requested for vehicle: {}", id);
        return ResponseEntity.ok(
                ApiResponse.ok(carfaxService.getSummary(id)));
    }

    // ── GET /api/vehicles/{id}/carfax/full — public ───────────────────────
    @GetMapping("/{id}/carfax/full")
    public ResponseEntity<ApiResponse<CarfaxFullDto>> full(
            @PathVariable UUID id) {
        log.info("✅ CarFax full report requested for vehicle: {}", id);
        return ResponseEntity.ok(
                ApiResponse.ok(carfaxService.getFull(id)));
    }
}