package org.example.project.repository;

import org.example.project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Integer> {
    @Query("""
            select p
            from Product p
            where lower(p.nameUz) like lower(concat('%', :keyword, '%'))
               or lower(p.nameRu) like lower(concat('%', :keyword, '%'))
               or lower(p.nameEng) like lower(concat('%', :keyword, '%'))
            """)
    Page<Product> search(String keyword, Pageable pageable);

    List<Product> findByCategoryIdAndIsAvailableTrue(Integer categoryId);
    Page<Product> findByIsAvailableTrue(Pageable pageable);
}
