package org.example.project.repository;

import org.example.project.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Admin search
    Page<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);
    
    // Admin filter by verification status
    Page<Users> findByEnabled(Boolean enabled, Pageable pageable);
    Long countByEnabled(Boolean enabled);
}
