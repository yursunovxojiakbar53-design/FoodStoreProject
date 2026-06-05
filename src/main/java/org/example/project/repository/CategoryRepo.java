package org.example.project.repository;

import org.example.project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepo extends JpaRepository<Category,Integer> {
    @Query("select coalesce(max(c.orderId),0) from Category c")
    Integer findMaxOrderId();

    List<Category> findByOrderIdGreaterThanOrderByOrderId(Integer deletedOrder);
    @Query("""
            select c
            from Category c
            where lower(c.nameUz) like lower(concat('%', :keyword, '%'))
               or lower(c.nameRu) like lower(concat('%', :keyword, '%'))
               or lower(c.nameEng) like lower(concat('%', :keyword, '%'))
            """)
    List<Category> search(String keyword);
}
