package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.PaymentDto;
import org.example.project.entity.Order;
import org.example.project.entity.Payment;
import org.example.project.entity.Users;
import org.example.project.enums.PaymentStatus;
import org.example.project.exception.ForbiddenException;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.OrderRepo;
import org.example.project.repository.PaymentRepo;
import org.example.project.repository.UsersRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UsersRepo usersRepo;
    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;

    private Users getUser(Authentication auth){
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public ApiResponse createPayment(Authentication authentication, PaymentDto dto) {


        Users user = getUser(authentication);

        Order order = orderRepo.findById(dto.getOrderId()).orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Order not found for user");
        }

        if (paymentRepo.findByOrder(order).isPresent()) {
            throw new IllegalStateException("Payment already exists for this order");
        }

        if (Double.compare(dto.getAmount(), order.getTotalPrice()) != 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(dto.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepo.save(payment);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment = paymentRepo.save(payment);

        return ApiResponse.builder()
                .message("Payment completed successfully")
                .status(true)
                .data(payment)
                .build();
    }
    public ApiResponse getByOrder(Integer orderId, Authentication auth){
        Users user = getUser(auth);
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        Payment payment = paymentRepo.findByOrder(order).orElseThrow(() -> new NotFoundException("Payment not found"));
        if (!payment.getOrder().getUser().getId().equals(user.getId()))
            throw new ForbiddenException("Bu to'lov sizga tegishli emas");
        return ApiResponse.builder().message("Payment retrieved").status(true).data(payment).build();
    }
}

