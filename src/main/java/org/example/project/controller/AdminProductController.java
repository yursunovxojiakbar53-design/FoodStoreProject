package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ProductDto;
import org.example.project.entity.Product;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.repository.ProductRepo;
import org.example.project.service.ProductService;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Admin Product Management Controller
 * Reuses ProductService for CRUD operations
 * Endpoint: /api/v1/admin/products
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepo productRepo;
    private final ProductService productService;

    /**
     * Get all products with pagination
     * GET /api/v1/admin/products?page=0&size=10
     */
    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepo.findAll(pageable);
        
        return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", true, products));
    }

    /**
     * Search products by keyword
     * GET /api/v1/admin/products/search?keyword=plov&page=0&size=10
     */
    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepo.search(keyword, pageable);
        
        return ResponseEntity.ok(new ApiResponse("Search results", true, products));
    }

    /**
     * Get single product by ID
     * GET /api/v1/admin/products/{id}
     */
    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        return ResponseEntity.ok(new ApiResponse("Product retrieved", true, product));
    }

    /**
     * Create new product
     * POST /api/v1/admin/products
     */
    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDto dto) {
        ApiResponse response = productService.addProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update product
     * PUT /api/v1/admin/products/{id}
     */
    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestBody @Valid ProductDto dto) {
        
        ApiResponse response = productService.updateProduct(dto, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete/Deactivate product
     * DELETE /api/v1/admin/products/{id}
     */
    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        ApiResponse response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }
}
