package com.mdau.proelitecars.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mdau.proelitecars.cloudinary.dto.SignRequest;
import com.mdau.proelitecars.cloudinary.dto.SignResponse;
import com.mdau.proelitecars.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.cloudinary.api-key}")
    private String apiKey;

    @Value("${app.cloudinary.cloud-name}")
    private String cloudName;

    // ── Sign an upload request for the frontend ───────────────────────────
    public SignResponse signUpload(SignRequest request) {
        try {
            long timestamp = System.currentTimeMillis() / 1000L;

            Map<String, Object> params = new HashMap<>();
            params.put("timestamp", timestamp);
            params.put("folder", request.getFolder());
            if (request.getPublicId() != null && !request.getPublicId().isBlank()) {
                params.put("public_id", request.getPublicId());
            }

            String signature = cloudinary.apiSignRequest(params,
                    cloudinary.config.apiSecret);

            log.info("✅ Cloudinary upload signed for folder: {}", request.getFolder());

            return SignResponse.builder()
                    .signature(signature)
                    .timestamp(timestamp)
                    .apiKey(apiKey)
                    .cloudName(cloudName)
                    .folder(request.getFolder())
                    .publicId(request.getPublicId())
                    .uploadUrl("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload")  // ADD THIS
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to sign Cloudinary upload: {}", e.getMessage(), e);
            throw new BusinessException("Failed to sign upload: " + e.getMessage());
        }
    }

    // ── Delete a single image by publicId ─────────────────────────────────
    public boolean deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) return false;
        try {
            Map result = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.emptyMap());
            String resultStr = String.valueOf(result.get("result"));
            if ("ok".equals(resultStr)) {
                log.info("✅ Cloudinary image deleted: {}", publicId);
                return true;
            } else {
                log.warn("⚠️ Cloudinary delete returned: {} for publicId: {}",
                        resultStr, publicId);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Failed to delete Cloudinary image {}: {}",
                    publicId, e.getMessage(), e);
            return false;
        }
    }

    // ── Delete multiple images — used on entity deletion ──────────────────
    public void deleteImages(List<String> publicIds) {
        if (publicIds == null || publicIds.isEmpty()) return;
        publicIds.forEach(this::deleteImage);
    }

    // ── Extract publicId from a Cloudinary URL ────────────────────────────
    public String extractPublicId(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank()) return null;
        try {
            // URL format: https://res.cloudinary.com/{cloud}/image/upload/v{ver}/{folder}/{publicId}.{ext}
            String path = cloudinaryUrl.substring(cloudinaryUrl.indexOf("/image/upload/") + 14);
            // Remove version segment if present (v1234567/)
            if (path.matches("v\\d+/.*")) {
                path = path.substring(path.indexOf('/') + 1);
            }
            // Remove extension
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex > 0) {
                path = path.substring(0, dotIndex);
            }
            return path;
        } catch (Exception e) {
            log.warn("⚠️ Could not extract publicId from URL: {}", cloudinaryUrl);
            return null;
        }
    }
}