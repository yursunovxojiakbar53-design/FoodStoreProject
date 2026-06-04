package org.example.project.repository;

import org.example.project.entity.Product;
import org.example.project.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
    List<Review> findAllByProduct(Product product);
}

