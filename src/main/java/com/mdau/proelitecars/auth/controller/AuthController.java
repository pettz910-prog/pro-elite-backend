package com.mdau.proelitecars.auth.controller;

import com.mdau.proelitecars.auth.dto.AuthResponse;
import com.mdau.proelitecars.auth.dto.AuthUserDto;
import com.mdau.proelitecars.auth.dto.LoginRequest;
import com.mdau.proelitecars.auth.dto.RefreshTokenRequest;
import com.mdau.proelitecars.auth.dto.SignupRequest;
import com.mdau.proelitecars.auth.service.AuthService;
import com.mdau.proelitecars.common.response.ApiResponse;
import com.mdau.proelitecars.common.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(
            @Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok("Tokens refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserDto>> me(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        AuthUserDto dto = authService.getMe(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}