package com.mdau.proelitecars.config;

import com.mdau.proelitecars.user.entity.Role;
import com.mdau.proelitecars.user.entity.User;
import com.mdau.proelitecars.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdmin();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("✅ Admin already exists: {}", adminEmail);
            return;
        }
        User admin = User.builder()
                .firstName("Super")
                .lastName("Admin")
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode("Admin@1234!"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.info("✅ Admin user seeded: {}", adminEmail);
    }
}