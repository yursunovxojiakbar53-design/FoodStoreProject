package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.PaymentDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> create(Authentication authentication, @RequestBody PaymentDto dto){
        ApiResponse apiResponse = paymentService.createPayment(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrder(@PathVariable Integer orderId){
        ApiResponse apiResponse = paymentService.getByOrder(orderId);
        return ResponseEntity.ok(apiResponse);
    }
}

