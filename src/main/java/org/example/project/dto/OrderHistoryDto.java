package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDto {
    private Integer orderId;
    private double totalAmount;
    private LocalDateTime date;
    private String orderStatus;
    private String deliverType;
    private List<OrderItemHistoryDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemHistoryDto {
        private String productName;
        private double quantity;
        private double price;
        private double lineTotal;
    }
}