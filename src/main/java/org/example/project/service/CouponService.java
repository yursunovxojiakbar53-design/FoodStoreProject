package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.CouponDto;
import org.example.project.entity.Coupon;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.CouponRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepo couponRepo;

    public ApiResponse create(CouponDto dto){
        Coupon coupon = Coupon.builder()
                .code(dto.getCode())
                .discountPercent(dto.getDiscountPercent())
                .minOrderAmount(dto.getMinOrderAmount())
                .expiresAt(dto.getExpiresAt())
                .isActive(dto.isActive())
                .build();
        couponRepo.save(coupon);
        return ApiResponse.builder().message("Coupon created").status(true).data(coupon).build();
    }

    public ApiResponse update(Integer id, CouponDto dto){
        Coupon coupon = couponRepo.findById(id).orElseThrow(() -> new NotFoundException("Coupon not found"));
        coupon.setCode(dto.getCode());
        coupon.setDiscountPercent(dto.getDiscountPercent());
        coupon.setMinOrderAmount(dto.getMinOrderAmount());
        coupon.setExpiresAt(dto.getExpiresAt());
        coupon.setActive(dto.isActive());
        couponRepo.save(coupon);
        return ApiResponse.builder().message("Coupon updated").status(true).data(coupon).build();
    }

    public ApiResponse delete(Integer id){
        Coupon coupon = couponRepo.findById(id).orElseThrow(() -> new NotFoundException("Coupon not found"));
        couponRepo.delete(coupon);
        return ApiResponse.builder().message("Coupon deleted").status(true).build();
    }

    public ApiResponse list(){
        List<Coupon> list = couponRepo.findAll();
        return ApiResponse.builder().message("Coupons retrieved").status(true).data(list).build();
    }

    public ApiResponse get(Integer id){
        Coupon coupon = couponRepo.findById(id).orElseThrow(() -> new NotFoundException("Coupon not found"));
        return ApiResponse.builder().message("Coupon retrieved").status(true).data(coupon).build();
    }

    public ApiResponse apply(String code, double amount){
        Coupon coupon = couponRepo.findByCode(code).orElseThrow(() -> new NotFoundException("Coupon not found"));
        if (!coupon.isActive()) return ApiResponse.builder().message("Coupon not active").status(false).build();
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(java.time.LocalDateTime.now())) return ApiResponse.builder().message("Coupon expired").status(false).build();
        if (amount < coupon.getMinOrderAmount()) return ApiResponse.builder().message("Order amount too small for coupon").status(false).build();
        double discount = amount * coupon.getDiscountPercent() / 100.0;
        return ApiResponse.builder().message("Coupon applied").status(true).data(discount).build();
    }
}

