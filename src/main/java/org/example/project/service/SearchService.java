package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Category;
import org.example.project.entity.Product;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.CategoryRepo;
import org.example.project.repository.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;

    public ApiResponse searchCategory(String keyword) {
        List<Category> categories = categoryRepo.search(keyword);
        return new ApiResponse("Categories found successfully", true, categories);
    }
    public ApiResponse searchProduct(String keyword, Pageable pageable) {
        Page<Product> products = productRepo.search(keyword, pageable);
        return new ApiResponse("Products found successfully", true, products);
    }

}
