package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderDto;
import org.example.project.entity.Order;
import org.example.project.enums.OrderStatus;
import org.example.project.extra.ApiResponse;
import org.example.project.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ApiResponse createOrder(@RequestBody OrderDto dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping
    public Page<Order> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return orderService.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Order getOne(@PathVariable Integer id) {
        return orderService.getOne(id);
    }

    @PutMapping("/{id}/status")
    public ApiResponse changeStatus(@PathVariable Integer id, @RequestParam OrderStatus status) {
        return orderService.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return orderService.delete(id);
    }
}