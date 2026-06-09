package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/open")
@RequiredArgsConstructor
public class OpenProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getAll(categoryId, page, size));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getOne(id));
    }
}
