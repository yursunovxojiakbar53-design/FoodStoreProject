package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderHistoryDto;
import org.example.project.entity.Order;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.OrderRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/open")
@RequiredArgsConstructor
public class OpenController {
    private final OrderRepo orderRepo;

    @GetMapping("/orders/history")
    public ResponseEntity<?> getOrderHistory(@RequestParam String phone){
        List<Order> orderList = orderRepo.findByPhoneNumber(phone);
        if (orderList.isEmpty()) return ResponseEntity.ok(new ApiResponse("Buyurtmalar topilmadi", false, null));


        List<OrderHistoryDto> result = orderList.stream().map(order -> {

            List<OrderHistoryDto.OrderItemHistoryDto> items = order.getOrderItems()
                    .stream()
                    .map(item -> OrderHistoryDto.OrderItemHistoryDto.builder()
                            .productName(item.getProduct().getName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .lineTotal(item.getPrice() * item.getQuantity())
                            .build())
                    .toList();

            return OrderHistoryDto.builder()
                    .orderId(order.getId())
                    .totalAmount(order.getTotalPrice())
                    .date(order.getCreatedAt())
                    .orderStatus(order.getOrderStatus().name())
                    .deliverType(order.getDeliverType().name())
                    .items(items)
                    .build();

        }).toList();

        return ResponseEntity.ok(result);

    }

}
