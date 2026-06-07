package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.project.enums.DeliverType;
import org.example.project.enums.OrderStatus;
import org.example.project.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Integer id;
    private String phoneNumber;
    private String message;
    private DeliverType deliverType;
    private PaymentType paymentType;
    private OrderStatus orderStatus;
    private double totalPrice;
    private Integer filialId;
    private Integer addressId;
    private List<OrderItemResponseDto> items;
    private LocalDateTime createdAt;
}

