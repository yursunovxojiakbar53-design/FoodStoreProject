package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderDto;
import org.example.project.dto.OrderItemDto;
import org.example.project.entity.*;
import org.example.project.enums.DeliverType;
import org.example.project.enums.OrderStatus;
import org.example.project.exception.ForbiddenException;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UsersRepo usersRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final AddressRepo addressRepo;
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final EmailService emailService;
    private final FilialRepo filialRepo;
    private final CouponRepo couponRepo;

    private Users getUser(Authentication auth) {
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("Foydalanuvchi topilmadi"));
    }

    @Transactional
    public ApiResponse createOrder(OrderDto dto, Authentication authentication) {

        Users user = getUser(authentication);

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            return new ApiResponse("Buyurtmada mahsulot yo'q", false, null);
        }


        Order order = new Order();
        order.setUser(user);
        order.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : user.getUsername());
        order.setMessage(dto.getMessage());
        order.setDeliverType(dto.getDeliverType());
        order.setPaymentType(dto.getPaymentType());
        order.setOrderStatus(OrderStatus.NEW);

        if (dto.getDeliverType() == DeliverType.DELEVER) {
            if (dto.getAddressId() == null) {
                return new ApiResponse("Yetkazib berish uchun manzil kiritilishi shart", false, null);
            }
            Address address = addressRepo.findByIdAndUserId(dto.getAddressId(), user.getId()).orElseThrow(() -> new NotFoundException("Manzil topilmadi yoki sizga tegishli emas"));
            order.setAddress(address);
        }

        if (dto.getDeliverType() == DeliverType.PICKUP) {
            if (dto.getFilialId() == null) {
                return new ApiResponse("Olib ketish uchun filial tanlanishi shart", false, null);
            }
            Filial filial = filialRepo.findById(dto.getFilialId()).orElseThrow(() -> new NotFoundException("Filial topilmadi"));
            order.setFilial(filial);
        }

        // 6. OrderItem lar va umumiy summani hisoblash
        double total = 0;
        double finalTotal = 0;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDto itemDto : dto.getItems()) {

            Product product = productRepo.findById(itemDto.getProductId()).orElseThrow(() -> new NotFoundException("Mahsulot topilmadi: ID = " + itemDto.getProductId()));

            if (!product.isAvailable()) {
                return new ApiResponse("Mahsulot mavjud emas: " + product.getName(), false, null);
            }

            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                return new ApiResponse("Mahsulot miqdori noto'g'ri: " + product.getName(), false, null);
            }

            double unitPrice = product.getDiscountPrice() > 0
                    ? product.getDiscountPrice()
                    : product.getPrice();

            double lineTotal = unitPrice * itemDto.getQuantity();
            total += lineTotal;
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(unitPrice);
            items.add(item);
        }

        double discount = 0;

        if (dto.getCouponCode() != null && !dto.getCouponCode().isBlank()) {

            Coupon coupon = couponRepo.findByCode(dto.getCouponCode()).orElse(null);

            if (coupon == null)
                return new ApiResponse("Kupon topilmadi", false, null);
            if (!coupon.isActive())
                return new ApiResponse("Kupon faol emas", false, null);
            if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now()))
                return new ApiResponse("Kupon muddati tugagan", false, null);
            if (total < coupon.getMinOrderAmount())
                return new ApiResponse("Minimal summa: " + coupon.getMinOrderAmount(), false, null);

            discount = total * coupon.getDiscountPercent() / 100.0;
        }

        finalTotal = total - discount;


        order.setOrderItems(items);
        order.setTotalPrice(finalTotal);

        orderRepo.save(order);

        // Cartni tozalash
        cartRepo.findByUsersId(user.getId()).ifPresent(cart -> {
            List<CartItem> cartItems = cartItemRepo.findByCartId(cart.getId());
            cartItemRepo.deleteAll(cartItems);
        });

        // Email xabar yuborish
        emailService.sendOrderConfirmation(
                user.getUsername(),
                order.getId(),
                total
        );

        return new ApiResponse("Buyurtma muvaffaqiyatli yaratildi", true, order);
    }

    public Page<Order> getAll(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size));
    }


    public ApiResponse getMyOrders(Authentication auth) {
        Users user = getUser(auth);
        List<Order> orders = orderRepo.findByUserId(user.getId());
        return new ApiResponse("OK", true, orders);
    }

    public ApiResponse getOne(Integer id, Authentication auth) {
        Users user = getUser(auth);

        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Buyurtma topilmadi: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Bu buyurtma sizga tegishli emas");
        }

        return new ApiResponse("OK", true, order);
    }

    public ApiResponse changeStatus(Integer id, OrderStatus status) {

        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Buyurtma topilmadi: " + id));

        order.setOrderStatus(status);
        orderRepo.save(order);

        emailService.sendOrderStatusUpdate(
                order.getUser().getUsername(),
                order.getId(),
                status.name()
        );

        return new ApiResponse("Status yangilandi: " + status, true, order);
    }

    public ApiResponse cancelOrder(Integer id, Authentication auth) {
        Users user = getUser(auth);

        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Buyurtma topilmadi: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Bu buyurtma sizga tegishli emas");
        }

        // Faqat NEW yoki PENDING holatdagi buyurtmani bekor qilish mumkin
        if (order.getOrderStatus() != OrderStatus.NEW && order.getOrderStatus() != OrderStatus.PENDING) {
            return new ApiResponse("You can't cancel because order already: " + order.getOrderStatus(), false, null);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(order);

        return new ApiResponse("Order canceled", true, order);
    }

    public ApiResponse delete(Integer id) {
        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Buyurtma topilmadi: " + id));
        orderRepo.delete(order);
        return new ApiResponse("Deleted", true, null);
    }


}