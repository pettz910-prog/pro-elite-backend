package com.mdau.proelitecars.cloudinary.controller;

import com.mdau.proelitecars.cloudinary.dto.SignRequest;
import com.mdau.proelitecars.cloudinary.dto.SignResponse;
import com.mdau.proelitecars.cloudinary.service.CloudinaryService;
import com.mdau.proelitecars.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    // ── POST /api/cloudinary/sign — STAFF or ADMIN ────────────────────────
    @PostMapping("/sign")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<SignResponse>> sign(
            @Valid @RequestBody SignRequest request) {
        SignResponse response = cloudinaryService.signUpload(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── DELETE /api/cloudinary/{publicId} — STAFF or ADMIN ───────────────
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable String publicId) {
        boolean deleted = cloudinaryService.deleteImage(publicId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("Image deleted successfully"));
        }
        return ResponseEntity.ok(ApiResponse.fail("Image could not be deleted or not found"));
    }
}