package org.example.project.entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.project.enums.DeliverType;
import org.example.project.enums.OrderStatus;
import org.example.project.extra.AbstractEntity;

import java.util.List;
import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private String phoneNumber;

    private String message;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliverType deliverType;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "filial_id")
    private Filial filial;
}
