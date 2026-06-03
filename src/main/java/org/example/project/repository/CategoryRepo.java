package org.example.project.repository;

import org.example.project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepo extends JpaRepository<Category,Integer> {
    @Query("select coalesce(max(c.orderId),0) from Category c")
    Integer findMaxOrderId();

    List<Category> findByOrderIdGreaterThanOrderByOrderId(Integer deletedOrder);
}
