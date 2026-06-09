package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Users;
import org.example.project.enums.Role;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.repository.UsersRepo;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin User Management Controller
 * Manage users, block/unblock, assign roles
 * Endpoint: /api/v1/admin/users
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UsersRepo usersRepo;

    /**
     * Get all users with pagination
     * GET /api/v1/admin/users?page=0&size=10
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = usersRepo.findAll(pageable);
        
        return ResponseEntity.ok(new ApiResponse("Users retrieved successfully", true, users));
    }

    /**
     * Search users by name or email
     * GET /api/v1/admin/users/search?query=john&page=0&size=10
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = usersRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable);
        
        return ResponseEntity.ok(new ApiResponse("Search results", true, users));
    }

    /**
     * Get single user details
     * GET /api/v1/admin/users/{id}
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable Integer id) {
        Users user = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        return ResponseEntity.ok(new ApiResponse("User details retrieved", true, user));
    }

    /**
     * Block/Unblock user
     * PUT /api/v1/admin/users/{id}/block?blocked=true
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable Integer id,
            @RequestParam Boolean blocked) {
        
        Users user = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if user has field for blocked status (may need to add)
        // For now, we disable the user if blocking
        if (blocked) {
            user.setEnabled(false);
        } else {
            user.setEnabled(true);
        }
        
        usersRepo.save(user);
        
        String message = blocked ? "User blocked successfully" : "User unblocked successfully";
        return ResponseEntity.ok(new ApiResponse(message, true, user));
    }

    /**
     * Get users by verification status
     * GET /api/v1/admin/users/filter/verified?verified=true&page=0&size=10
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @GetMapping("/filter/verified")
    public ResponseEntity<?> getUsersByVerificationStatus(
            @RequestParam Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = usersRepo.findByEnabled(verified, pageable);
        
        String status = verified ? "verified" : "unverified";
        return ResponseEntity.ok(new ApiResponse("Users filtered by verification: " + status, true, users));
    }

    /**
     * Delete user (hard delete)
     * DELETE /api/v1/admin/users/{id}
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        Users user = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        usersRepo.deleteById(id);
        
        return ResponseEntity.ok(new ApiResponse("User deleted successfully", true, null));
    }

    /**
     * Get user count statistics
     * GET /api/v1/admin/users/stats/count
     */
    @RequirePermission(Perms.MANAGE_USERS)
    @GetMapping("/stats/count")
    public ResponseEntity<?> getUserStatistics() {
        long totalUsers = usersRepo.count();
        long verifiedUsers = usersRepo.countByEnabled(true);
        long unverifiedUsers = totalUsers - verifiedUsers;
        
        return ResponseEntity.ok(new ApiResponse("User statistics", true, 
            new ApiResponse.UserStats(totalUsers, verifiedUsers, unverifiedUsers)));
    }

    /**
     * Helper class for statistics
     */
    public static class UserStats {
        public Long totalUsers;
        public Long verifiedUsers;
        public Long unverifiedUsers;

        public UserStats(Long totalUsers, Long verifiedUsers, Long unverifiedUsers) {
            this.totalUsers = totalUsers;
            this.verifiedUsers = verifiedUsers;
            this.unverifiedUsers = unverifiedUsers;
        }
    }
}
