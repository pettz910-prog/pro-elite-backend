package com.mdau.proelitecars.user.controller;

import com.mdau.proelitecars.common.response.ApiResponse;
import com.mdau.proelitecars.inquiry.dto.InquiryDto;
import com.mdau.proelitecars.inquiry.entity.InquiryStatus;
import com.mdau.proelitecars.inquiry.repository.InquiryRepository;
import com.mdau.proelitecars.inquiry.service.InquiryMapper;
import com.mdau.proelitecars.user.dto.DashboardDto;
import com.mdau.proelitecars.vehicle.dto.VehicleCardDto;
import com.mdau.proelitecars.vehicle.entity.VehicleStatus;
import com.mdau.proelitecars.vehicle.repository.VehicleRepository;
import com.mdau.proelitecars.vehicle.service.VehicleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VehicleRepository vehicleRepository;
    private final InquiryRepository inquiryRepository;
    private final VehicleMapper vehicleMapper;
    private final InquiryMapper inquiryMapper;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<DashboardDto>> dashboard() {

        // ── Vehicle stats ─────────────────────────────────────────────────
        DashboardDto.VehicleStats vehicleStats = DashboardDto.VehicleStats.builder()
                .total(vehicleRepository.count())
                .available(vehicleRepository.countByStatus(VehicleStatus.AVAILABLE))
                .sold(vehicleRepository.countByStatus(VehicleStatus.SOLD))
                .reserved(vehicleRepository.countByStatus(VehicleStatus.RESERVED))
                .hidden(vehicleRepository.countByStatus(VehicleStatus.HIDDEN))
                .build();

        // ── Inquiry stats ─────────────────────────────────────────────────
        Instant startOfToday = Instant.now().truncatedTo(ChronoUnit.DAYS);
        DashboardDto.InquiryStats inquiryStats = DashboardDto.InquiryStats.builder()
                .total(inquiryRepository.count())
                .newCount(inquiryRepository.countByStatus(InquiryStatus.NEW))
                .inProgress(inquiryRepository.countByStatus(InquiryStatus.IN_PROGRESS))
                .resolved(inquiryRepository.countByStatus(InquiryStatus.RESOLVED))
                .closed(inquiryRepository.countByStatus(InquiryStatus.CLOSED))
                .newToday(inquiryRepository.countCreatedSince(startOfToday))
                .build();

        // ── Recent vehicles (last 5) ───────────────────────────────────────
        List<VehicleCardDto> recentVehicles = vehicleRepository
                .findAll(PageRequest.of(0, 5,
                        Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(vehicleMapper::toCardDto)
                .toList();

        // ── Recent inquiries (last 5) — notes eagerly fetched ─────────────
        List<InquiryDto> recentInquiries = inquiryRepository
                .findRecentWithNotes(PageRequest.of(0, 5))
                .stream()
                .map(inquiryMapper::toDto)
                .toList();

        DashboardDto dashboard = DashboardDto.builder()
                .vehicles(vehicleStats)
                .inquiries(inquiryStats)
                .recentVehicles(recentVehicles)
                .recentInquiries(recentInquiries)
                .build();

        log.info("✅ Dashboard data fetched");
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }
}