package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderDto;
import org.example.project.dto.OrderItemDto;
import org.example.project.entity.Order;
import org.example.project.entity.OrderItem;
import org.example.project.entity.Product;
import org.example.project.entity.Users;
import org.example.project.enums.DeliverType;
import org.example.project.enums.OrderStatus;
import org.example.project.exception.GlobalExceptionHandler;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.OrderItemRepo;
import org.example.project.repository.OrderRepo;
import org.example.project.repository.ProductRepo;
import org.example.project.repository.UsersRepo;
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

    private final UsersRepo usersRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final GlobalExceptionHandler globalExceptionHandler;
    public ApiResponse createOrder(OrderDto dto) {

        Order order = new Order();
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setMessage(dto.getMessage());
        order.setDeliverType(dto.getDeliverType());
        order.setOrderStatus(OrderStatus.NEW);
        order.setPaymentType(dto.getPaymentType());

        // filial va address bog'lash (qisqartirilgan)
        // order.setFilial(...); order.setAddress(...);

        double total = 0;                          // ← shu yerda yig'amiz
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDto itemDto : dto.getItems()) {

            Product product = productRepo.findById(itemDto.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            double unitPrice = product.getCurrentPrice() > 0 ? product.getCurrentPrice() : product.getPrice();

            double lineTotal = unitPrice * itemDto.getQuantity();

            total += lineTotal;

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(unitPrice);
            items.add(item);
        }

        order.setOrderItems(items);
        order.setTotalPrice(total);

        orderRepo.save(order);

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