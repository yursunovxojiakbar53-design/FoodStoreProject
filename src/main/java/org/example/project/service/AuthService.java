package org.example.project.service;

import lombok.RequiredArgsConstructor;

import org.example.project.dto.LoginDto;
import org.example.project.dto.RegisterDto;
import org.example.project.entity.Users;
import org.example.project.enums.Role;
import org.example.project.exception.AlreadyExistException;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.UserRoleRepo;
import org.example.project.repository.UsersRepo;
import org.example.project.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepo usersRepo;
    private final UserRoleRepo userRoleRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public ApiResponse register(RegisterDto users) {
        if (usersRepo.existsByEmail(users.getEmail())) throw  new AlreadyExistException("Already exist");

        if (!users.getPassword().equals(users.getPrePassword())) return new ApiResponse("Wrong password", false);
        int emailCode = 100000 + new Random().nextInt(900000);
        Users user = Users.builder()
                .email(users.getEmail())
                .password(passwordEncoder.encode(users.getPassword()))
                .name(users.getFirstName())
                .enabled(false)
                .emailCode(emailCode)
                .role(userRoleRepo.findByRole(Role.ROLE_USER))
                .build();
        usersRepo.save(user);
        emailService.sendVerificationEmail(users.getEmail(), emailCode);
        return new ApiResponse("User registered successfully", true);
    }

    public ApiResponse login(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            Users users = usersRepo.findByEmail(loginDto.getEmail()).orElseThrow(() -> new NotFoundException("Invalid credentials"));
            String token = jwtService.generateToken(users);
            return new ApiResponse("User logged in successfully", true,token);
        }catch (Exception exception){
            return new ApiResponse("Invalid credentials", false);
        }

    }

    public ApiResponse verifyEmail(String email, Integer code) {
        Users user = usersRepo.findByEmail(email).orElse(null);
        if (user == null) return new ApiResponse("Not found", false);
        if (!code.equals(user.getEmailCode())) {
            return new ApiResponse("Wrong email code", false);
        }

        user.setEnabled(true);
        user.setEmailCode(null);

        usersRepo.save(user);
        return new ApiResponse("Email tasdiqlandi! Endi login qiling.", true);
    }

    public ApiResponse verify(String email, Integer code) {
        return verifyEmail(email, code);
    }
}
