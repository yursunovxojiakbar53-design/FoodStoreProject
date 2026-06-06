package org.example.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderDto;
import org.example.project.entity.Order;
import org.example.project.enums.OrderStatus;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.OrderService;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @RequirePermission(Perms.CREATE_ORDER)
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody  @Valid OrderDto dto, Authentication authentication) {
        ApiResponse apiResponse = orderService.createOrder(dto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @RequirePermission(Perms.VIEW_OWN_ORDERS)
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(Authentication auth) {
        ApiResponse apiResponse = orderService.getMyOrders(auth);
        return ResponseEntity.ok(apiResponse);
    }


    @RequirePermission(Perms.VIEW_ALL_ORDERS)
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Order> result = orderService.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    @RequirePermission(Perms.CANCEL_OWN_ORDER)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id, Authentication auth) {
        ApiResponse apiResponse = orderService.cancelOrder(id, auth);
        return ResponseEntity.ok(apiResponse);
    }


    @RequirePermission(Perms.VIEW_OWN_ORDERS)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id, Authentication auth) {
        ApiResponse apiResponse = orderService.getOne(id, auth);
        return ResponseEntity.ok(apiResponse);
    }

    @RequirePermission(Perms.UPDATE_ORDER_STATUS)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Integer id, @RequestParam OrderStatus status) {
        ApiResponse apiResponse = orderService.changeStatus(id, status);
        return ResponseEntity.ok(apiResponse);
    }

    @RequirePermission(Perms.CANCEL_OWN_ORDER)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id,Authentication auth) {
        ApiResponse apiResponse = orderService.delete(id,auth);
        return ResponseEntity.ok(apiResponse);
    }
}