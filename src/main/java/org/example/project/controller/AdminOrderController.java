package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AdminOrderDto;
import org.example.project.entity.Order;
import org.example.project.enums.OrderStatus;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.repository.OrderRepo;
import org.example.project.service.OrderService;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Order Management Controller
 * Reuses OrderService for order operations
 * Endpoint: /api/v1/admin/orders
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderRepo orderRepo;

    /**
     * Get all orders with pagination
     * GET /api/v1/admin/orders?page=0&size=10
     */
    @RequirePermission(Perms.VIEW_ALL_ORDERS)
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminOrderDto> dto =
                orderRepo.findAll(pageable)
                        .map(o -> new AdminOrderDto(
                                o.getId(),
                                o.getUser().getName(),
                                o.getPhoneNumber(),
                                o.getOrderStatus(),
                                o.getAddress().getTitle()
                        ));
        
        return ResponseEntity.ok(new ApiResponse("Orders retrieved successfully", true, dto));
    }

    /**
     * Get orders by status
     * GET /api/v1/admin/orders/filter?status=PENDING&page=0&size=10
     */
    @RequirePermission(Perms.VIEW_ALL_ORDERS)
    @GetMapping("/filter")
    public ResponseEntity<?> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepo.findByOrderStatus(status, pageable);
        
        return ResponseEntity.ok(new ApiResponse("Orders filtered by status", true, orders));
    }

    /**
     * Get single order details
     * GET /api/v1/admin/orders/{id}
     */
    @RequirePermission(Perms.VIEW_ALL_ORDERS)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        return ResponseEntity.ok(new ApiResponse("Order details retrieved", true, order));
    }

    /**
     * Update order status
     * PUT /api/v1/admin/orders/{id}/status?status=ON_THE_WAY
     */
    @RequirePermission(Perms.UPDATE_ORDER_STATUS)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status) {
        
        ApiResponse response = orderService.changeStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel order
     * PUT /api/v1/admin/orders/{id}/cancel
     */
    @RequirePermission(Perms.UPDATE_ORDER_STATUS)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setOrderStatus(OrderStatus.CANCELED);
        if (reason != null) {
            order.setMessage(reason);
        }
        orderRepo.save(order);
        
        return ResponseEntity.ok(new ApiResponse("Order cancelled successfully", true, order));
    }

    /**
     * Delete order
     * DELETE /api/v1/admin/orders/{id}
     */
    @RequirePermission(Perms.UPDATE_ORDER_STATUS)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        orderRepo.deleteById(id);
        
        return ResponseEntity.ok(new ApiResponse("Order deleted successfully", true, null));
    }
}
