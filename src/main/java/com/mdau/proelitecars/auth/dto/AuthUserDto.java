package com.mdau.proelitecars.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthUserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String phone;
}