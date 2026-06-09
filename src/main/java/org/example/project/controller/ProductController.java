package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ProductDto;
import jakarta.validation.Valid;
import org.example.project.entity.Product;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.repository.ProductRepo;
import org.example.project.repository.UsersRepo;
import org.example.project.service.ProductService;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final UsersRepo usersRepo;
    private final ProductRepo productRepo;
    private final ProductService productService;

    @RequirePermission(Perms.VIEW_PRODUCTS)
    @GetMapping
    public Page<Product> getAll(@RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepo.findAll(pageable);
    }


    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid ProductDto product) {
        ApiResponse apiResponse=productService.addProduct(product);
        return ResponseEntity.ok(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid ProductDto product) {
        ApiResponse apiResponse=productService.updateProduct(product,id);
        return ResponseEntity.ok(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_PRODUCTS)
    @DeleteMapping("/{id}")
     public ResponseEntity<?> delete(@PathVariable Integer id) {
        ApiResponse apiResponse=productService.deleteProduct(id);
        return ResponseEntity.ok(apiResponse);
    }



}
