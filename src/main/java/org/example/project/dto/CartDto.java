package org.example.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private Integer id;
    private Integer usersId;
    private List<CartItemDto> items;
    private Double totalPrice;
}

