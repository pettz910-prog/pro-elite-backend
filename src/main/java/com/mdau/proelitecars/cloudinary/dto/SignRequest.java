package com.mdau.proelitecars.cloudinary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignRequest {
    @NotBlank(message = "Folder is required")
    private String folder;

    private String publicId;
}