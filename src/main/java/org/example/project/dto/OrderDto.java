package org.example.project.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.project.enums.DeliverType;
import org.example.project.enums.PaymentType;

import java.util.List;

@Data
public class OrderDto {

    private String phoneNumber;

    private String message;

    @NotNull(message = "Mahsulotlar bo'sh bo'lmasligi kerak")
    private List<OrderItemDto> items;

    private Integer addressId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Yetkazish turi tanlanishi kerak")
    private DeliverType deliverType;

    private Integer filialId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "To'lov turi tanlanishi kerak")
    private PaymentType paymentType;

    private String couponCode;

}
