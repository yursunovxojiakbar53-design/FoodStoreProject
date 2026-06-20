package org.example.project.entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.project.enums.PaymentStatus;
import org.example.project.enums.PaymentType;
import org.example.project.extra.AbstractEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users users;


    @OneToOne
    private Order order;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String transactionId;
}
