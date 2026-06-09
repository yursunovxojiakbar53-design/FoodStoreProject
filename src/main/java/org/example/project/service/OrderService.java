package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.OrderDto;
import org.example.project.dto.OrderItemDto;
import org.example.project.entity.*;
import org.example.project.dto.OrderItemResponseDto;
import org.example.project.dto.OrderResponseDto;
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
                return new ApiResponse("Mahsulot mavjud emas: " + product.getNameUz(), false, null);
            }

            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                return new ApiResponse("Mahsulot miqdori noto'g'ri: " + product.getNameUz(), false, null);
            }

            // stock tracking faqat stockQuantity aniq belgilangan mahsulotlar uchun
            if (product.getStockQuantity() != null) {
                int stock = product.getStockQuantity();
                if (stock < itemDto.getQuantity()) {
                    return new ApiResponse("Zaxira yetarli emas: " + product.getNameUz() + " (mavjud: " + stock + ", so'ralgan: " + itemDto.getQuantity() + ")", false, null);
                }
                product.setStockQuantity(stock - itemDto.getQuantity());
                if (product.getStockQuantity() == 0) {
                    product.setAvailable(false);
                }
                productRepo.save(product);
            }

            double unitPrice = product.getDiscountPrice() > 0 ? product.getDiscountPrice() : product.getPrice();

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
                finalTotal

        );

        OrderResponseDto responseDto = toDto(order);
        return new ApiResponse("Buyurtma muvaffaqiyatli yaratildi", true, responseDto);
    }

    public Page<Order> getAll(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size));
    }


    public ApiResponse getMyOrders(Authentication auth) {
        Users user = getUser(auth);
        List<Order> orders = orderRepo.findByUserId(user.getId());
        List<OrderResponseDto> dtos = new ArrayList<>();
        for (Order o : orders) dtos.add(toDto(o));
        return new ApiResponse("OK", true, dtos);
    }

    public ApiResponse getOne(Integer id, Authentication auth) {
        Users user = getUser(auth);

        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Buyurtma topilmadi: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Bu buyurtma sizga tegishli emas");
        }

        OrderResponseDto dto = toDto(order);
        return new ApiResponse("OK", true, dto);
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

        OrderResponseDto dto = toDto(order);
        return new ApiResponse("Status yangilandi: " + status, true, dto);
    }
    @Transactional
    public ApiResponse cancelOrder(Integer id, Authentication auth) {
        Users user = getUser(auth);

        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Buyurtma topilmadi: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Bu buyurtma sizga tegishli emas");
        }

        if (order.getOrderStatus() != OrderStatus.NEW && order.getOrderStatus() != OrderStatus.PENDING) {
            return new ApiResponse("You can't cancel because order already: " + order.getOrderStatus(), false, null);
        }

        // ✅ YANGI: zaxirani qaytarish
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            product.setStockQuantity((int) (currentStock + item.getQuantity()));
            if (product.getStockQuantity() > 0) {
                product.setAvailable(true);
            }
            productRepo.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepo.save(order);

        OrderResponseDto dto = toDto(order);
        return new ApiResponse("Order canceled", true, dto);
    }



    public ApiResponse delete(Integer id, Authentication auth) {
        Users user = getUser(auth);
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Topilmadi"));

        if (!order.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("Bu buyurtma sizga tegishli emas");

        orderRepo.delete(order);
        return new ApiResponse("O'chirildi", true, null);
    }

    // --- Mapping helpers ---
    private OrderResponseDto toDto(Order order) {
        if (order == null) return null;
        List<OrderItemResponseDto> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItem it : order.getOrderItems()) {
                OrderItemResponseDto itDto = OrderItemResponseDto.builder()
                        .productId(it.getProduct() != null ? it.getProduct().getId() : null)
                        .productName(it.getProduct() != null ? it.getProduct().getName() : null)
                        .quantity(it.getQuantity())
                        .price(it.getPrice())
                        .build();
                items.add(itDto);
            }
        }

        return OrderResponseDto.builder()
                .id(order.getId())
                .phoneNumber(order.getPhoneNumber())
                .message(order.getMessage())
                .deliverType(order.getDeliverType())
                .paymentType(order.getPaymentType())
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice())
                .filialId(order.getFilial() != null ? order.getFilial().getId() : null)
                .addressId(order.getAddress() != null ? order.getAddress().getId() : null)
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }


}