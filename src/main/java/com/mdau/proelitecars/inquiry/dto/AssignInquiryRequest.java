package com.mdau.proelitecars.inquiry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignInquiryRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    private String userName;
}