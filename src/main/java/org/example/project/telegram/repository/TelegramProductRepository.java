package org.example.project.telegram.repository;

import org.example.project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelegramProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.isAvailable = true")
    Page<Product> findAvailable(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isAvailable = true")
    Page<Product> findAvailableByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);
}
