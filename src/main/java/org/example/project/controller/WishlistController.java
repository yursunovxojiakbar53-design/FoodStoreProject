package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.WishlistDto;
import jakarta.validation.Valid;
import org.example.project.enums.Permission;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.WishlistService;
import org.example.project.valid.RequirePermission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
@RequirePermission(Perms.MANAGE_OWN_WISHLIST)
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<?> add(Authentication authentication, @RequestBody @Valid WishlistDto dto){
        ApiResponse apiResponse = wishlistService.addToWishlist(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(Authentication authentication, @PathVariable Integer id){
        ApiResponse apiResponse = wishlistService.removeFromWishlist(authentication, id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication authentication){
        ApiResponse apiResponse = wishlistService.getWishlist(authentication);
        return ResponseEntity.ok(apiResponse);
    }
}

