package org.example.project.repository;

import org.example.project.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order,Integer> {
    List<Order> findByUserId(Integer id);

    List<Order> findByPhoneNumber(String phoneNumber);
}
