package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ProductDto;
import org.example.project.entity.Product;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.ProductRepo;
import org.example.project.repository.UsersRepo;
import org.example.project.service.ProductService;
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

    @GetMapping
    public Page<Product> getAll(
            @RequestParam(defaultValue = "0")  int page,  // bosmasa 0-sahifa
            @RequestParam(defaultValue = "10") int size   // bosmasa 10 ta
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepo.findAll(pageable);
    }


    @PostMapping
    public ResponseEntity<?> save(@RequestBody ProductDto product) {
        ApiResponse apiResponse=productService.addProduct(product);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody ProductDto product) {
        ApiResponse apiResponse=productService.updateProduct(product,id);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
     public ResponseEntity<?> delete(@PathVariable Integer id) {
        ApiResponse apiResponse=productService.deleteProduct(id);
        return ResponseEntity.ok(apiResponse);
    }
}
