package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.AddToCartDto;
import org.example.project.dto.CartDto;
import org.example.project.dto.CartItemDto;
import org.example.project.dto.UpdateCartItemDto;
import org.example.project.entity.Cart;
import org.example.project.entity.CartItem;
import org.example.project.entity.Product;
import org.example.project.entity.Users;
import org.example.project.exception.ForbiddenException;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.CartItemRepo;
import org.example.project.repository.CartRepo;
import org.example.project.repository.ProductRepo;
import org.example.project.repository.UsersRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final UsersRepo usersRepo;

    // Authentication'dan user olish
    private Users getUserFromAuthentication(Authentication auth) {
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not authenticated"));
    }

    // Foydalanuvchi uchun savat olish
    @Transactional
    public ResponseEntity<ApiResponse> getCartByUserId(Authentication auth) {
        Users user = getUserFromAuthentication(auth);

        Cart cart = cartRepo.findByUsersId(user.getId()).orElse(null);

        if (cart == null) {
            cart = Cart.builder().users(user).build();
            cartRepo.save(cart);
        }

        return ResponseEntity.ok(new ApiResponse("Cart retrieved successfully", true, convertToDto(cart)));
    }

    // Savatga mahsulot qo'shish
    @Transactional
    public ResponseEntity<ApiResponse> addToCart(Authentication auth, AddToCartDto dto) {
        Users user = getUserFromAuthentication(auth);

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Foydalanuvchining savatini olish yoki yangi savat yaratish
        Cart cart = cartRepo.findByUsersId(user.getId())
                .orElse(null);

        if (cart == null) {
            cart = Cart.builder().users(user).build();
            cartRepo.save(cart);
        }

        // Agar mahsulot savatda bo'lsa, miqdorini oshirish
        List<CartItem> cartItems = cartItemRepo.findByCartId(cart.getId());
        Optional<CartItem> existingItem = cartItemRepo.findByCartIdAndProductId(cart.getId(), dto.getProductId());

        if (existingItem.isPresent()) {                 // ← null emas, isPresent()
            CartItem item = existingItem.get();         // ← ichidagi CartItem'ни olamiz
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            cartItemRepo.save(item);                    // ← CartItem'ni saqlaymiz
            return ResponseEntity.ok(
                    new ApiResponse("Product quantity updated", true, convertToDto(cart)));
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .build();
            cartItemRepo.save(newItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Product added to cart", true, convertToDto(cart)));
        }
    }

    // Savatdan mahsulot o'chirish
    @Transactional
    public ResponseEntity<ApiResponse> removeFromCart(Authentication auth, Integer cartItemId) {
        Users user = getUserFromAuthentication(auth);
        CartItem cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUsers().getId().equals(user.getId())) {
            throw new ForbiddenException("You can't remove items from another user's cart");
        }

        Integer cartId = cartItem.getCart().getId();
        cartItemRepo.delete(cartItem);

        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new NotFoundException("Cart not found"));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse("Product removed from cart", true, convertToDto(cart)));
    }

    // Savat elementini yangilash (miqdor o'zgartirish)
    @Transactional
    public ResponseEntity<ApiResponse> updateCartItem(Authentication auth, Integer cartItemId, UpdateCartItemDto dto) {
        Users user = getUserFromAuthentication(auth);

        CartItem cartItem = cartItemRepo.findById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUsers().getId().equals(user.getId())) {
            throw new ForbiddenException("You can't update items from another user's cart");
        }

        cartItem.setQuantity(dto.getQuantity());
        cartItemRepo.save(cartItem);

        Cart cart = cartRepo.findById(cartItem.getCart().getId()).orElseThrow(() -> new NotFoundException("Cart not found"));

        return ResponseEntity.ok(new ApiResponse("Cart item updated", true, convertToDto(cart)));
    }

    // Savatni tozalash
    @Transactional
    public ResponseEntity<ApiResponse> clearCart(Authentication auth) {
        Users user = getUserFromAuthentication(auth);

        Cart cart = cartRepo.findByUsersId(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));

        List<CartItem> cartItems = cartItemRepo.findByCartId(cart.getId());
        cartItemRepo.deleteAll(cartItems);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse("Cart cleared", true, convertToDto(cart)));
    }

    // Helper method: CartDto-ga o'tkazish
    private CartDto convertToDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUsersId(cart.getUsers().getId());

        List<CartItem> cartItems = cartItemRepo.findByCartId(cart.getId());
        List<CartItemDto> itemDtos = new ArrayList<>();
        double totalPrice = 0;

        for (CartItem item : cartItems) {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setId(item.getId());
            itemDto.setCartId(cart.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());

            double price = item.getProduct().getCurrentPrice() > 0 ? item.getProduct().getDiscountPrice() : item.getProduct().getPrice();

            itemDto.setProductPrice(price);
            itemDto.setQuantity(item.getQuantity());
            itemDto.setLineTotal(price * item.getQuantity());

            totalPrice += itemDto.getLineTotal();
            itemDtos.add(itemDto);
        }

        dto.setItems(itemDtos);
        dto.setTotalPrice(totalPrice);

        return dto;
    }
}
