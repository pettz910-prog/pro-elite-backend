package com.mdau.proelitecars.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final UUID id;
    private final String email;
    private final String role;
}