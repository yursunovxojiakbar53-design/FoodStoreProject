package org.example.project.extra;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.UserRole;
import org.example.project.enums.Role;
import org.example.project.repository.UserRoleRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRoleRepo userRoleRepo;

    @Override
    public void run(String @NonNull ... args) {

        for (Role role : Role.values()) {

            if (!userRoleRepo.existsByRole(role)) {

                UserRole userRole = new UserRole();
                userRole.setRole(role);

                userRoleRepo.save(userRole);
            }
        }
    }
}