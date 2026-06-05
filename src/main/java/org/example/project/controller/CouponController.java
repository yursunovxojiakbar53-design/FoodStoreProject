package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.CouponDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CouponDto dto){
        ApiResponse apiResponse = couponService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CouponDto dto){
        ApiResponse apiResponse = couponService.update(id, dto);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        ApiResponse apiResponse = couponService.delete(id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> list(){
        ApiResponse apiResponse = couponService.list();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id){
        ApiResponse apiResponse = couponService.get(id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/apply")
    public ResponseEntity<?> apply(@RequestParam String code, @RequestParam double amount){
        ApiResponse apiResponse = couponService.apply(code, amount);
        return ResponseEntity.ok(apiResponse);
    }
}

