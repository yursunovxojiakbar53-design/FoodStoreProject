package org.example.project.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String firstName;
    private String email;
    private String password;
    private String prePassword;
}
