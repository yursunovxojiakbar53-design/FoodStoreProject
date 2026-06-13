package org.example.project.dto;

import org.example.project.enums.OrderStatus;

public record AdminOrderDto(
        Integer id,
        String customerName,
        String phoneNumber,
        OrderStatus orderStatus,
        String addressTitle
) {}