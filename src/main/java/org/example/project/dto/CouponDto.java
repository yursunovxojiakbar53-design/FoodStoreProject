package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private String code;
    private double discountPercent;
    private double minOrderAmount;
    private LocalDateTime expiresAt;
    private boolean isActive;
}

