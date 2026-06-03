package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderDto;
import org.example.project.dto.OrderItemDto;
import org.example.project.entity.Order;
import org.example.project.entity.OrderItem;
import org.example.project.entity.Product;
import org.example.project.enums.OrderStatus;
import org.example.project.exception.GlobalExceptionHandler;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.OrderItemRepo;
import org.example.project.repository.OrderRepo;
import org.example.project.repository.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final GlobalExceptionHandler globalExceptionHandler;

    public ApiResponse createOrder(OrderDto dto) {

        Order order = new Order();
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setMessage(dto.getMessage());
        order.setOrderStatus(OrderStatus.NEW);

        order = orderRepo.save(order);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDto itemDto : dto.getItems()) {
            Product product = productRepo.findById(itemDto.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            OrderItem item = new OrderItem();

            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            items.add(item);
        }

        orderItemRepo.saveAll(items);

        return new ApiResponse("Order created", true, order);
    }

    public Page<Order> getAll(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size));
    }


    public Order getOne(Integer id) {
        return orderRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    public ApiResponse changeStatus(Integer id, OrderStatus status) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        order.setOrderStatus(status);
        orderRepo.save(order);

        return new ApiResponse("Status updated", true, order);
    }

        public ApiResponse delete(Integer id){

            Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Not found"+id));
            orderRepo.delete(order);
            return new ApiResponse("Deleted", true, null);
        }
    }
}