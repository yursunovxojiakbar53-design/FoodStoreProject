package org.example.project.repository;

import org.example.project.entity.Order;
import org.example.project.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order,Integer> {
    List<Order> findByUserId(Integer id);

    List<Order> findByPhoneNumber(String phoneNumber);
    
    // Admin filter by order status with pagination
    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
}
