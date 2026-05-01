package com.mdau.proelitecars.user.service;

import com.mdau.proelitecars.common.exception.BusinessException;
import com.mdau.proelitecars.common.exception.ResourceNotFoundException;
import com.mdau.proelitecars.user.dto.*;
import com.mdau.proelitecars.user.entity.Role;
import com.mdau.proelitecars.user.entity.User;
import com.mdau.proelitecars.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Get all staff (ADMIN only) ────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserDto> getStaff(Role role) {
        List<User> users = (role != null)
                ? userRepository.findAllByRole(role)
                : userRepository.findAllByRoleNot(Role.CUSTOMER);
        return users.stream().map(this::toDto).toList();
    }

    // ── Get all customers (ADMIN only) ────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserDto> getCustomers() {
        return userRepository.findAllByRole(Role.CUSTOMER)
                .stream().map(this::toDto).toList();
    }

    // ── Create staff member (ADMIN only) ─────────────────────────────────
    @Transactional
    public UserDto createStaff(CreateStaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Email already registered: " + request.getEmail());
        }
        if (request.getRole() == Role.CUSTOMER) {
            throw new BusinessException(
                    "Cannot create a CUSTOMER via staff endpoint");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phone(request.getPhone())
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("✅ Staff created: {} [{}]", saved.getEmail(), saved.getRole());
        return toDto(saved);
    }

    // ── Update staff member (ADMIN only) ─────────────────────────────────
    @Transactional
    public UserDto updateStaff(UUID id, UpdateStaffRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName()  != null) user.setLastName(request.getLastName());
        if (request.getPhone()     != null) user.setPhone(request.getPhone());
        if (request.getEnabled()   != null) user.setEnabled(request.getEnabled());

        if (request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(
                        "Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            if (request.getRole() == Role.CUSTOMER) {
                throw new BusinessException(
                        "Cannot demote a staff member to CUSTOMER");
            }
            user.setRole(request.getRole());
        }

        User saved = userRepository.save(user);
        log.info("✅ Staff updated: {} [{}]", saved.getEmail(), saved.getRole());
        return toDto(saved);
    }

    // ── Delete staff member (ADMIN only) ─────────────────────────────────
    @Transactional
    public void deleteStaff(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (user.getRole() == Role.CUSTOMER) {
            throw new BusinessException("Use customer endpoint for customers");
        }
        userRepository.delete(user);
        log.info("✅ Staff deleted: {}", id);
    }

    // ── Change password (ADMIN only) ──────────────────────────────────────
    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("✅ Password changed for user: {}", user.getEmail());
    }

    // ── Mapper ────────────────────────────────────────────────────────────
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}