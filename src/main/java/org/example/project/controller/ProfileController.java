package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Users;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.UsersRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UsersRepo usersRepo;

    // Joriy (login qilgan) foydalanuvchi ma'lumotlari — createdAt, name, email, role
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(new ApiResponse("Avtorizatsiya talab qilinadi", false));
        }
        Users user = usersRepo.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse("Foydalanuvchi topilmadi", false));
        }
        return ResponseEntity.ok(new ApiResponse("OK", true, user));
    }
}
