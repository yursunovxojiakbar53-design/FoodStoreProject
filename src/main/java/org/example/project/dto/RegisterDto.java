package org.example.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {
    @NotBlank(message = "Ism bo'sh bo'lmasligi kerak")
    private String firstName;

    @NotBlank(message = "Email bo'sh bo'lmasligi kerak")
    @Email(message = "Email noto'g'ri")
    private String email;

    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
    @Size(min = 6, message = "Parol kamida 6 ta belgi")
    private String password;

    @NotBlank(message = "Parol tasdiqlash bo'sh bo'lmasligi kerak")
    private String prePassword;
}
