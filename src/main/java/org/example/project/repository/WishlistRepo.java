package org.example.project.repository;

import org.example.project.entity.Product;
import org.example.project.entity.Users;
import org.example.project.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepo extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findAllByUsers(Users user);
    Optional<Wishlist> findByUsersAndProduct(Users user, Product product);
}

