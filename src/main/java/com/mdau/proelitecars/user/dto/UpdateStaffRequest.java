package com.mdau.proelitecars.user.dto;

import com.mdau.proelitecars.user.entity.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateStaffRequest {
    private String firstName;
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private Role role;
    private String phone;
    private Boolean enabled;
}