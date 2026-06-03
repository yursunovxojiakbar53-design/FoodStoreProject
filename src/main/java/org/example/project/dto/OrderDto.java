package org.example.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private String phoneNumber;

    private String message;

    private List<OrderItemDto> items;
}
