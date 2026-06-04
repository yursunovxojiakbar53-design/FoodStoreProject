package org.example.project.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.example.project.extra.AbstractEntity;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends AbstractEntity {
    private String code;
    private double discountPercent;
    private double minOrderAmount;
    private LocalDateTime expiresAt;
    private boolean isActive;
}
