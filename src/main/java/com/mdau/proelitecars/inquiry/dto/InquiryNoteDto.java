package com.mdau.proelitecars.inquiry.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class InquiryNoteDto {
    private UUID id;
    private String body;
    private UUID authorId;
    private String authorName;
    private Instant createdAt;
}