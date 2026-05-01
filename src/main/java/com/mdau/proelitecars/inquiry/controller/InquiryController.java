package com.mdau.proelitecars.inquiry.controller;

import com.mdau.proelitecars.common.response.ApiResponse;
import com.mdau.proelitecars.common.security.AuthenticatedUser;
import com.mdau.proelitecars.inquiry.dto.*;
import com.mdau.proelitecars.inquiry.entity.InquiryStatus;
import com.mdau.proelitecars.inquiry.entity.InquiryType;
import com.mdau.proelitecars.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // ── POST /api/inquiries — public ──────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<InquiryDto>> create(
            @Valid @RequestBody CreateInquiryRequest request) {
        InquiryDto dto = inquiryService.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Inquiry submitted successfully", dto));
    }

    // ── GET /api/inquiries — STAFF or ADMIN ───────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<Page<InquiryDto>>> findAll(
            @RequestParam(required = false) InquiryStatus status,
            @RequestParam(required = false) InquiryType type,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 &&
                sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, sortField));

        Page<InquiryDto> result = inquiryService.findAll(
                status, type, assignedTo, pageable);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── GET /api/inquiries/{id} — STAFF or ADMIN ──────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<InquiryDto>> findById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(inquiryService.findById(id)));
    }

    // ── PATCH /api/inquiries/{id}/status — STAFF or ADMIN ─────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<InquiryDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody InquiryStatusRequest request) {
        InquiryDto dto = inquiryService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Status updated", dto));
    }

    // ── PATCH /api/inquiries/{id}/assign — STAFF or ADMIN ─────────────────
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<InquiryDto>> assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignInquiryRequest request) {
        InquiryDto dto = inquiryService.assign(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Inquiry assigned", dto));
    }

    // ── POST /api/inquiries/{id}/notes — STAFF or ADMIN ───────────────────
    @PostMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<InquiryNoteDto>> addNote(
            @PathVariable UUID id,
            @Valid @RequestBody AddNoteRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        InquiryNoteDto note = inquiryService.addNote(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Note added", note));
    }
}