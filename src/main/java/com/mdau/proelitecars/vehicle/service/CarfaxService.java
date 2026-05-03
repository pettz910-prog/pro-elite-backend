package com.mdau.proelitecars.vehicle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdau.proelitecars.common.exception.ResourceNotFoundException;
import com.mdau.proelitecars.vehicle.dto.CarfaxFullDto;
import com.mdau.proelitecars.vehicle.dto.CarfaxSummaryDto;
import com.mdau.proelitecars.vehicle.entity.CarfaxReport;
import com.mdau.proelitecars.vehicle.entity.Vehicle;
import com.mdau.proelitecars.vehicle.repository.CarfaxReportRepository;
import com.mdau.proelitecars.vehicle.repository.VehicleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarfaxService {

    private final VehicleRepository vehicleRepository;
    private final CarfaxReportRepository carfaxReportRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.carfax.api-key:}")
    private String apiKey;

    @Value("${app.carfax.api-url:https://api.carfax.com/v1}")
    private String apiUrl;

    @Value("${app.carfax.enabled:false}")
    private boolean enabled;

    @Value("${app.carfax.cache-days:7}")
    private int cacheDays;

    private static final String NO_VIN_MESSAGE =
            "Vehicle history available for physical and personalized checks. "
            + "Please contact us to schedule an inspection.";

    private OkHttpClient httpClient;

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        log.info("✅ CarfaxService initialized — API enabled: {}", enabled);
    }

    // ── GET /api/vehicles/{id}/carfax/summary ─────────────────────────────
    @Transactional
    public CarfaxSummaryDto getSummary(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", vehicleId));

        // No VIN — return placeholder
        if (vehicle.getVin() == null || vehicle.getVin().isBlank()) {
            return CarfaxSummaryDto.builder()
                    .available(false)
                    .unavailableMessage(NO_VIN_MESSAGE)
                    .build();
        }

        String vin = vehicle.getVin();
        CarfaxReport report = getOrFetchReport(vin);

        return CarfaxSummaryDto.builder()
                .vin(vin)
                .available(true)
                .ownerCount(report.getOwnerCount())
                .hasAccidents(report.getHasAccidents())
                .accidentCount(report.getAccidentCount())
                .cleanTitle(report.getCleanTitle())
                .serviceRecordCount(report.getServiceRecordCount())
                .lastServiceDate(report.getLastServiceDate())
                .reportUrl(report.getReportUrl())
                .badgeUrl(report.getBadgeUrl())
                .source(report.getSource().name())
                .build();
    }

    // ── GET /api/vehicles/{id}/carfax/full ────────────────────────────────
    @Transactional
    public CarfaxFullDto getFull(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", vehicleId));

        // No VIN — return placeholder
        if (vehicle.getVin() == null || vehicle.getVin().isBlank()) {
            return CarfaxFullDto.builder()
                    .available(false)
                    .unavailableMessage(NO_VIN_MESSAGE)
                    .build();
        }

        String vin = vehicle.getVin();
        CarfaxReport report = getOrFetchReport(vin);

        return CarfaxFullDto.builder()
                .vin(vin)
                .available(true)
                .ownerCount(report.getOwnerCount())
                .hasAccidents(report.getHasAccidents())
                .accidentCount(report.getAccidentCount())
                .cleanTitle(report.getCleanTitle())
                .serviceRecordCount(report.getServiceRecordCount())
                .lastServiceDate(report.getLastServiceDate())
                .useTypes(report.getUseTypes())
                .recalls(report.getRecalls())
                .odometerReadings(report.getOdometerReadings())
                .reportUrl(report.getReportUrl())
                .badgeUrl(report.getBadgeUrl())
                .source(report.getSource().name())
                .fetchedAt(report.getFetchedAt().toString())
                .build();
    }

    // ── Core: get from cache or fetch ─────────────────────────────────────
    private CarfaxReport getOrFetchReport(String vin) {
        Optional<CarfaxReport> cached = carfaxReportRepository.findByVin(vin);

        // Return cached if still fresh
        if (cached.isPresent() && !cached.get().isStale(cacheDays)) {
            log.debug("✅ CarFax cache hit for VIN: {}", vin);
            return cached.get();
        }

        // Fetch from API if enabled
        if (enabled && apiKey != null && !apiKey.isBlank()) {
            try {
                CarfaxReport fetched = fetchFromApi(vin);
                // Save or update cache
                if (cached.isPresent()) {
                    copyReportFields(fetched, cached.get());
                    return carfaxReportRepository.save(cached.get());
                }
                return carfaxReportRepository.save(fetched);
            } catch (Exception e) {
                log.error("❌ CarFax API call failed for VIN {}: {}",
                        vin, e.getMessage());
                // Fall through to free link if API fails
            }
        }

        // Fallback — return free CarFax link
        if (cached.isPresent()) {
            return cached.get();
        }

        CarfaxReport freeReport = buildFreeReport(vin);
        return carfaxReportRepository.save(freeReport);
    }

    // ── Fetch from CarFax Dealer API ──────────────────────────────────────
    @SuppressWarnings("unchecked")
    private CarfaxReport fetchFromApi(String vin) throws Exception {
        Request request = new Request.Builder()
                .url(apiUrl + "/history/" + vin)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("CarFax API returned: "
                        + response.code());
            }

            String body = response.body().string();
            Map<String, Object> data = objectMapper.readValue(body, Map.class);

            log.info("✅ CarFax API response for VIN: {}", vin);

            return CarfaxReport.builder()
                    .vin(vin)
                    .ownerCount((Integer) data.get("ownerCount"))
                    .hasAccidents((Boolean) data.get("hasAccidents"))
                    .accidentCount((Integer) data.getOrDefault(
                            "accidentCount", 0))
                    .cleanTitle((Boolean) data.getOrDefault(
                            "cleanTitle", true))
                    .serviceRecordCount((Integer) data.getOrDefault(
                            "serviceRecordCount", 0))
                    .lastServiceDate((String) data.get("lastServiceDate"))
                    .useTypes((List<String>) data.getOrDefault(
                            "useTypes", new ArrayList<>()))
                    .recalls((List<String>) data.getOrDefault(
                            "recalls", new ArrayList<>()))
                    .odometerReadings(parseOdometerReadings(data))
                    .reportUrl((String) data.get("reportUrl"))
                    .badgeUrl((String) data.get("badgeUrl"))
                    .fetchedAt(Instant.now())
                    .source(CarfaxReport.CarfaxSource.DEALER_API)
                    .build();
        }
    }

    // ── Build free-tier report (link only) ────────────────────────────────
    private CarfaxReport buildFreeReport(String vin) {
        String freeUrl = "https://www.carfax.com/VehicleHistory/ar20/p/"
                + "vehicle-history-report.cfm?vin=" + vin;
        return CarfaxReport.builder()
                .vin(vin)
                .reportUrl(freeUrl)
                .fetchedAt(Instant.now())
                .source(CarfaxReport.CarfaxSource.FREE)
                .build();
    }

    // ── Copy updated fields onto existing cached entity ───────────────────
    private void copyReportFields(CarfaxReport from, CarfaxReport to) {
        to.setOwnerCount(from.getOwnerCount());
        to.setHasAccidents(from.getHasAccidents());
        to.setAccidentCount(from.getAccidentCount());
        to.setCleanTitle(from.getCleanTitle());
        to.setServiceRecordCount(from.getServiceRecordCount());
        to.setLastServiceDate(from.getLastServiceDate());
        to.setUseTypes(from.getUseTypes());
        to.setRecalls(from.getRecalls());
        to.setOdometerReadings(from.getOdometerReadings());
        to.setReportUrl(from.getReportUrl());
        to.setBadgeUrl(from.getBadgeUrl());
        to.setFetchedAt(from.getFetchedAt());
        to.setSource(from.getSource());
    }

    // ── Parse odometer readings from raw API map ──────────────────────────
    @SuppressWarnings("unchecked")
    private List<CarfaxReport.OdometerReading> parseOdometerReadings(
            Map<String, Object> data) {
        try {
            List<Map<String, Object>> raw =
                    (List<Map<String, Object>>) data.get("odometerReadings");
            if (raw == null) return new ArrayList<>();
            return raw.stream()
                    .map(r -> new CarfaxReport.OdometerReading(
                            (String) r.get("date"),
                            (Integer) r.get("miles")))
                    .toList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}