package com.mdau.proelitecars.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddNoteRequest {
    @NotBlank(message = "Note body is required")
    private String body;
}