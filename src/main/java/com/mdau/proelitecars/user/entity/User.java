package com.mdau.proelitecars.user.entity;

import com.mdau.proelitecars.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email",      columnList = "email",      unique = true),
        @Index(name = "idx_users_role",       columnList = "role"),
        @Index(name = "idx_users_enabled",    columnList = "enabled"),
        @Index(name = "idx_users_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}