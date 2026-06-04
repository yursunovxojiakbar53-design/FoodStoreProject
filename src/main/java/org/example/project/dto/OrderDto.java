package org.example.project.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.example.project.enums.DeliverType;

import java.util.List;

@Data
public class OrderDto {
    private String phoneNumber;

    private String message;


    private List<OrderItemDto> items;

    private Integer addressId;

    @Enumerated(EnumType.STRING)
    private DeliverType deliverType;

    private Integer filialId;


}
