package org.example.project.repository;

import org.example.project.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepo extends JpaRepository<Coupon, Integer> {
    Optional<Coupon> findByCode(String code);
}

