package com.mdau.proelitecars.cloudinary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignResponse {
    private String signature;
    private long timestamp;
    private String apiKey;
    private String cloudName;
    private String folder;
    private String publicId;
}