package org.example.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project.dto.AddToCartDto;
import org.example.project.dto.UpdateCartItemDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse> getCart(Authentication auth) {
        return cartService.getCartByUserId(auth);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addToCart(Authentication auth, @RequestBody @Valid AddToCartDto dto) {
        return cartService.addToCart(auth, dto);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse> removeFromCart(Authentication auth, @PathVariable Integer cartItemId) {
        return cartService.removeFromCart(auth, cartItemId);
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<ApiResponse> updateCartItem(Authentication auth, @PathVariable Integer cartItemId, @RequestBody @Valid UpdateCartItemDto dto) {
        return cartService.updateCartItem(auth, cartItemId, dto);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart(Authentication auth) {
        return cartService.clearCart(auth);
    }
}
