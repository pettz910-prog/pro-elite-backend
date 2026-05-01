package com.mdau.proelitecars.inquiry.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InquiryDto {
    private UUID id;
    private UUID vehicleId;
    private String vehicleTitle;
    private String customerName;
    private String email;
    private String phone;
    private String message;
    private String type;
    private String source;
    private String status;
    private UUID assignedToId;
    private String assignedToName;
    private List<InquiryNoteDto> notes;
    private Instant createdAt;
    private Instant updatedAt;
}