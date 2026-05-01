package com.mdau.proelitecars.inquiry.dto;

import com.mdau.proelitecars.inquiry.entity.InquiryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InquiryStatusRequest {
    @NotNull(message = "Status is required")
    private InquiryStatus status;
}