package org.example.project.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Integer id;
    private Integer cartId;
    private Integer productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double lineTotal;
}

