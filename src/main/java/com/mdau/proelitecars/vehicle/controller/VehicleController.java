package com.mdau.proelitecars.vehicle.controller;

import com.mdau.proelitecars.common.response.ApiResponse;
import com.mdau.proelitecars.vehicle.dto.*;
import com.mdau.proelitecars.vehicle.entity.*;
import com.mdau.proelitecars.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // ── GET /api/vehicles — public, paginated, filtered ───────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Page<VehicleCardDto>>> findAll(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minMileage,
            @RequestParam(required = false) Integer maxMileage,
            @RequestParam(required = false) VehicleCondition condition,
            @RequestParam(required = false) BodyStyle bodyStyle,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Transmission transmission,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) VehicleBadge badge,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "12")  int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1
                && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, sortField));

        Page<VehicleCardDto> result = vehicleService.findAll(
                make, model, minYear, maxYear,
                minPrice, maxPrice, minMileage, maxMileage,
                condition, bodyStyle, fuelType, transmission,
                location, status, badge, keyword, pageable);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── GET /api/vehicles/featured — public ───────────────────────────────
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<VehicleCardDto>>> featured() {
        return ResponseEntity.ok(
                ApiResponse.ok(vehicleService.findFeatured()));
    }

    // ── GET /api/vehicles/search/meta — public ────────────────────────────
    // Returns distinct makes, body styles, years, price range, mileage range
    // from live inventory. Powers homepage dropdowns and filter sliders.
    @GetMapping("/search/meta")
    public ResponseEntity<ApiResponse<VehicleSearchMetaDto>> searchMeta() {
        return ResponseEntity.ok(
                ApiResponse.ok(vehicleService.getSearchMeta()));
    }

    // ── GET /api/vehicles/search/makes — public ───────────────────────────
    @GetMapping("/search/makes")
    public ResponseEntity<ApiResponse<List<String>>> makes() {
        return ResponseEntity.ok(
                ApiResponse.ok(vehicleService.findDistinctMakes()));
    }

    // ── GET /api/vehicles/search/models?make= — public ───────────────────
    @GetMapping("/search/models")
    public ResponseEntity<ApiResponse<List<String>>> models(
            @RequestParam String make) {
        return ResponseEntity.ok(
                ApiResponse.ok(vehicleService.findDistinctModelsByMake(make)));
    }

    // ── GET /api/vehicles/{id} — public ───────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDto>> findById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(vehicleService.findById(id)));
    }

    // ── POST /api/vehicles — STAFF or ADMIN ───────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDto>> create(
            @Valid @RequestBody CreateVehicleRequest request) {
        VehicleDto dto = vehicleService.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle created", dto));
    }

    // ── PUT /api/vehicles/{id} — STAFF or ADMIN ───────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        VehicleDto dto = vehicleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle updated", dto));
    }

    // ── PATCH /api/vehicles/{id}/status — STAFF or ADMIN ─────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleStatusRequest request) {
        VehicleDto dto = vehicleService.updateStatus(
                id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Status updated", dto));
    }

    // ── DELETE /api/vehicles/{id} — STAFF or ADMIN ────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}