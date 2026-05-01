package com.mdau.proelitecars.inquiry.dto;

import com.mdau.proelitecars.inquiry.entity.InquirySource;
import com.mdau.proelitecars.inquiry.entity.InquiryType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateInquiryRequest {

    private UUID vehicleId;
    private String vehicleTitle;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String message;

    private InquiryType type;

    private InquirySource source;
}