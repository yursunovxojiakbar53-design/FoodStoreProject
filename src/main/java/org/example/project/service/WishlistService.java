package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.WishlistDto;
import org.example.project.entity.Product;
import org.example.project.entity.Users;
import org.example.project.entity.Wishlist;
import org.example.project.exception.AlreadyExistException;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.ProductRepo;
import org.example.project.repository.UsersRepo;
import org.example.project.repository.WishlistRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final UsersRepo usersRepo;
    private final ProductRepo productRepo;
    private final WishlistRepo wishlistRepo;

    private Users getUser(Authentication auth){
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public ApiResponse addToWishlist(Authentication authentication, WishlistDto dto){
        Users user = getUser(authentication);
        Product product = productRepo.findById(dto.getProductId()).orElseThrow(() -> new NotFoundException("Product not found"));
        if (wishlistRepo.findByUserAndProduct(user, product).isPresent()) throw new AlreadyExistException("Already in wishlist");
        Wishlist wishlist = Wishlist.builder().user(user).product(product).build();
        wishlistRepo.save(wishlist);
        return ApiResponse.builder().message("Added to wishlist").status(true).data(wishlist).build();
    }

    public ApiResponse removeFromWishlist(Authentication authentication, Integer id){
        Users user = getUser(authentication);
        Wishlist wishlist = wishlistRepo.findById(id).orElseThrow(() -> new NotFoundException("Wishlist item not found"));
        if (!wishlist.getUser().getId().equals(user.getId())) throw new NotFoundException("Wishlist item not found for user");
        wishlistRepo.delete(wishlist);
        return ApiResponse.builder().message("Removed from wishlist").status(true).build();
    }

    public ApiResponse getWishlist(Authentication authentication){
        Users user = getUser(authentication);
        List<Wishlist> list = wishlistRepo.findAllByUser(user);
        return ApiResponse.builder().message("Wishlist retrieved").status(true).data(list).build();
    }
}

