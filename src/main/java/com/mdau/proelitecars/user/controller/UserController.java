package com.mdau.proelitecars.user.controller;

import com.mdau.proelitecars.common.response.ApiResponse;
import com.mdau.proelitecars.user.dto.*;
import com.mdau.proelitecars.user.entity.Role;
import com.mdau.proelitecars.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── GET /api/staff?role= — ADMIN only ─────────────────────────────────
    @GetMapping("/api/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getStaff(
            @RequestParam(required = false) Role role) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getStaff(role)));
    }

    // ── GET /api/customers — ADMIN only ───────────────────────────────────
    @GetMapping("/api/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getCustomers()));
    }

    // ── POST /api/staff — ADMIN only ──────────────────────────────────────
    @PostMapping("/api/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {
        UserDto dto = userService.createStaff(request);
        return ResponseEntity.ok(ApiResponse.ok("Staff member created", dto));
    }

    // ── PUT /api/staff/{id} — ADMIN only ──────────────────────────────────
    @PutMapping("/api/staff/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffRequest request) {
        UserDto dto = userService.updateStaff(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Staff member updated", dto));
    }

    // ── DELETE /api/staff/{id} — ADMIN only ───────────────────────────────
    @DeleteMapping("/api/staff/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID id) {
        userService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    // ── PUT /api/users/{id}/password — ADMIN only ─────────────────────────
    @PutMapping("/api/users/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Password updated successfully"));
    }
}