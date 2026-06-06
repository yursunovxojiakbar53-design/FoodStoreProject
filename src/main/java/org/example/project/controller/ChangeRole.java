package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.UserRole;
import org.example.project.entity.Users;
import org.example.project.enums.Role;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.repository.UserRoleRepo;
import org.example.project.repository.UsersRepo;
import org.example.project.valid.RequirePermission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/changeRole")
@RequiredArgsConstructor
public class ChangeRole {
    private final UsersRepo usersRepo;
    private final UserRoleRepo userRoleRepo;


    @RequirePermission(Perms.CHANGE_ROLE)
    @PutMapping("/{id}")
    public ResponseEntity<?> changeUsersRole(@PathVariable Integer id, @RequestParam Role role){
        Users user = usersRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        UserRole userRole = userRoleRepo.findByRole(role)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Role topilmadi"));
        user.setRole(new HashSet<>(Set.of(userRole)));
        usersRepo.save(user);
        return ResponseEntity.ok(new ApiResponse("Rol o'zgartirildi: " + role, true, null));

    }
}
