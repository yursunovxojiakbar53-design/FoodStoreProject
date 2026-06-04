package org.example.project.repository;

import org.example.project.entity.Order;
import org.example.project.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrder(Order order);
}

