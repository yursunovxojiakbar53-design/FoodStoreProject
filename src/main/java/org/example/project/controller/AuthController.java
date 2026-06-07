package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.LoginDto;
import org.example.project.dto.RegisterDto;
import jakarta.validation.Valid;
import org.example.project.extra.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final org.example.project.service.AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto users) {
        ApiResponse response = authService.register(users);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        ApiResponse response = authService.login(loginDto);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email, @RequestParam String code) {
        ApiResponse response = authService.verify(email, code);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }
}
