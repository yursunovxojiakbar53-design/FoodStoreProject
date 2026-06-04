package org.example.project.entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.project.enums.PaymentStatus;
import org.example.project.extra.AbstractEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AbstractEntity {
    @OneToOne
    private Order order;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
