package com.mdau.proelitecars.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String phone;
    private boolean enabled;
    private Instant createdAt;
}