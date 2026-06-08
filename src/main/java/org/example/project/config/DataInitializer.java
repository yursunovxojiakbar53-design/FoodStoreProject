package org.example.project.config;


import lombok.RequiredArgsConstructor;
import org.example.project.entity.UserRole;
import org.example.project.entity.Users;
import org.example.project.enums.Role;
import org.example.project.repository.UserRoleRepo;
import org.example.project.repository.UsersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Creates a default SuperAdmin user on startup if it doesn't exist.
 * Safe to run multiple times (checks by email).
 */
@Component
@RequiredArgsConstructor
public class  DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsersRepo usersRepo;
    private final UserRoleRepo userRoleRepo;
    private final PasswordEncoder passwordEncoder;

    // Credentials to create - change here if necessary
    private static final String ADMIN_EMAIL = "yursunovxojiakbar53@gmail.com";

    @Override
    public void run(String... args) throws Exception {

        try {
            if (usersRepo.existsByEmail(ADMIN_EMAIL)) {
                log.info("SuperAdmin already exists: {}", ADMIN_EMAIL);
                // ensure enabled and has role
                Optional<Users> opt = usersRepo.findByEmail(ADMIN_EMAIL);
                if (opt.isPresent()) {
                    Users u = opt.get();
                    if (!u.isEnabled()) {
                        u.setEnabled(true);
                        usersRepo.save(u);
                        log.info("Enabled existing SuperAdmin account");
                    }
                }
                return;
            }

            // Ensure role entity exists
            UserRole userRole;
            if (userRoleRepo.existsByRole(Role.ROLE_SUPER_ADMIN)) {
                Set<UserRole> roles = userRoleRepo.findByRole(Role.ROLE_SUPER_ADMIN);
                userRole = roles.iterator().next();
            } else {
                userRole = UserRole.builder().role(Role.ROLE_SUPER_ADMIN).build();
                userRole = userRoleRepo.save(userRole);
            }

            String ADMIN_PASSWORD = "123qwe&&";
            Users admin = Users.builder()
                    .name("Super Admin")
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .role(Set.of(userRole))
                    .enabled(true)
                    .build();

            usersRepo.save(admin);
            log.info("Created SuperAdmin: {}", ADMIN_EMAIL);
        } catch (Exception e) {
            log.error("Error creating SuperAdmin", e);
        }
    }
}

