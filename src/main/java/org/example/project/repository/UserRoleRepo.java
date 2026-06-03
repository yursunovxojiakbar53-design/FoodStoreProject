package org.example.project.repository;


import org.example.project.entity.UserRole;
import org.example.project.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserRoleRepo extends JpaRepository<UserRole, Long> {
    Set<UserRole> findByRole(Role role);

}
