package org.example.project.telegram.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.*;
import org.example.project.entity.*;
import org.example.project.enums.DeliverType;
import org.example.project.enums.OrderStatus;
import org.example.project.enums.PaymentType;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.*;
import org.example.project.service.*;
import org.example.project.telegram.config.TelegramBotProperties;
import org.example.project.telegram.entity.TelegramUser;
import org.example.project.telegram.repository.TelegramProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodStoreFacadeService {

    private final UsersRepo usersRepo;
    private final ProductRepo productRepo;
    private final TelegramProductRepository telegramProductRepository;
    private final CategoryRepo categoryRepo;
    private final ReviewRepo reviewRepo;
    private final CartService cartService;
    private final WishlistService wishlistService;
    private final OrderService orderService;
    private final AddressService addressService;
    private final CouponService couponService;
    private final AboutAsRepo aboutAsRepo;
    private final FilialRepo filialRepo;
    private final OrderRepo orderRepo;
    private final TelegramBotProperties properties;

    public Authentication auth(TelegramUser tgUser) {
        Users user = usersRepo.findById(tgUser.getBackendUserId()).orElseThrow();
        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());
    }

    public Page<Product> getProducts(int page) {
        return telegramProductRepository.findAvailable(PageRequest.of(page, properties.getPageSize()));
    }

    public Page<Product> getProductsByCategory(int categoryId, int page) {
        return telegramProductRepository.findAvailableByCategoryId(categoryId, PageRequest.of(page, properties.getPageSize()));
    }

    public Optional<Product> getProduct(int id) {
        return productRepo.findById(id);
    }

    public Page<Category> getCategories(int page) {
        return categoryRepo.findAll(PageRequest.of(page, properties.getPageSize()));
    }

    public List<Review> getProductReviews(Product product) {
        return reviewRepo.findAllByProduct(product);
    }

    public CartDto getCart(TelegramUser tgUser) {
        ResponseEntity<ApiResponse> response = cartService.getCartByUserId(auth(tgUser));
        return (CartDto) response.getBody().getData();
    }

    public ApiResponse addToCart(TelegramUser tgUser, int productId, int qty) {
        AddToCartDto dto = new AddToCartDto();
        dto.setProductId(productId);
        dto.setQuantity(qty);
        return cartService.addToCart(auth(tgUser), dto).getBody();
    }

    public ApiResponse updateCartItem(TelegramUser tgUser, int cartItemId, int qty) {
        UpdateCartItemDto dto = new UpdateCartItemDto();
        dto.setQuantity(qty);
        return cartService.updateCartItem(auth(tgUser), cartItemId, dto).getBody();
    }

    public ApiResponse removeCartItem(TelegramUser tgUser, int cartItemId) {
        return cartService.removeFromCart(auth(tgUser), cartItemId).getBody();
    }

    public ApiResponse clearCart(TelegramUser tgUser) {
        return cartService.clearCart(auth(tgUser)).getBody();
    }

    public ApiResponse addToWishlist(TelegramUser tgUser, int productId) {
        WishlistDto dto = new WishlistDto();
        dto.setProductId(productId);
        return wishlistService.addToWishlist(auth(tgUser), dto);
    }

    @SuppressWarnings("unchecked")
    public List<Wishlist> getWishlist(TelegramUser tgUser) {
        ApiResponse response = wishlistService.getWishlist(auth(tgUser));
        return (List<Wishlist>) response.getData();
    }

    public ApiResponse removeWishlist(TelegramUser tgUser, int wishlistId) {
        return wishlistService.removeFromWishlist(auth(tgUser), wishlistId);
    }

    @SuppressWarnings("unchecked")
    public List<OrderResponseDto> getMyOrders(TelegramUser tgUser) {
        ApiResponse response = orderService.getMyOrders(auth(tgUser));
        return (List<OrderResponseDto>) response.getData();
    }

    public ApiResponse getOrder(TelegramUser tgUser, int orderId) {
        return orderService.getOne(orderId, auth(tgUser));
    }

    public ApiResponse cancelOrder(TelegramUser tgUser, int orderId) {
        return orderService.cancelOrder(orderId, auth(tgUser));
    }

    @SuppressWarnings("unchecked")
    public List<Address> getAddresses(TelegramUser tgUser) {
        ApiResponse response = addressService.getAddresses(auth(tgUser));
        return (List<Address>) response.getData();
    }

    public ApiResponse addAddress(TelegramUser tgUser, AddressDto dto) {
        return addressService.addAddress(auth(tgUser), dto);
    }

    public ApiResponse deleteAddress(TelegramUser tgUser, int addressId) {
        return addressService.deleteAddress(auth(tgUser), addressId);
    }

    @SuppressWarnings("unchecked")
    public List<Coupon> getCoupons() {
        ApiResponse response = couponService.list();
        return (List<Coupon>) response.getData();
    }

    public ApiResponse applyCoupon(String code, double amount) {
        return couponService.apply(code, amount);
    }

    public ApiResponse createOrder(TelegramUser tgUser, OrderDto dto) {
        return orderService.createOrder(dto, auth(tgUser));
    }

    public List<AboutAs> getAboutInfo() {
        return aboutAsRepo.findAll();
    }

    public List<Filial> getFilials() {
        return filialRepo.findAll();
    }

    public Page<Order> getAllOrders(int page) {
        return orderService.getAll(page, properties.getPageSize());
    }

    public ApiResponse changeOrderStatus(int orderId, OrderStatus status) {
        return orderService.changeStatus(orderId, status);
    }

    public OrderDto buildOrderFromCart(TelegramUser tgUser, String phone, DeliverType deliverType,
                                       PaymentType paymentType, Integer addressId, Integer filialId,
                                       String couponCode, String message) {
        CartDto cart = getCart(tgUser);
        OrderDto dto = new OrderDto();
        dto.setPhoneNumber(phone);
        dto.setMessage(message);
        dto.setDeliverType(deliverType);
        dto.setPaymentType(paymentType);
        dto.setAddressId(addressId);
        dto.setFilialId(filialId);
        dto.setCouponCode(couponCode);

        List<OrderItemDto> items = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItemDto item : cart.getItems()) {
                OrderItemDto oi = new OrderItemDto();
                oi.setProductId(item.getProductId());
                oi.setQuantity(item.getQuantity());
                items.add(oi);
            }
        }
        dto.setItems(items);
        return dto;
    }

    public long countOrdersByStatus(OrderStatus status) {
        return orderRepo.findAll().stream().filter(o -> o.getOrderStatus() == status).count();
    }

    public Optional<Order> getOrderById(int orderId) {
        return orderRepo.findById(orderId);
    }

    public java.io.File getProductImageFile(Product product) {
        if (product.getAttachment() == null || product.getAttachment().getPath() == null) {
            return null;
        }
        java.io.File file = new java.io.File(product.getAttachment().getPath());
        return file.exists() ? file : null;
    }
}
