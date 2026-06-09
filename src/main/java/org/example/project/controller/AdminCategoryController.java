package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.CategoryDto;
import org.example.project.dto.CategoryReorderDto;
import org.example.project.entity.Category;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.repository.CategoryRepo;
import org.example.project.service.CategoryService;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Admin Category Management Controller
 * Reuses CategoryService for CRUD operations
 * Endpoint: /api/v1/admin/categories
 */
@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryRepo categoryRepo;

    /**
     * Get all categories with pagination
     * GET /api/v1/admin/categories?page=0&size=10
     */
    @RequirePermission(Perms.MANAGE_CATEGORIES)
    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Category> categories = categoryService.getAll(page, size);
        
        return ResponseEntity.ok(new ApiResponse("Categories retrieved successfully", true, categories));
    }

    /**
     * Get single category by ID
     * GET /api/v1/admin/categories/{id}
     */
    @RequirePermission(Perms.MANAGE_CATEGORIES)
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Integer id) {
        Category category = categoryService.getOne(id);
        
        return ResponseEntity.ok(new ApiResponse("Category retrieved", true, category));
    }

    /**
     * Create new category
     * POST /api/v1/admin/categories
     */
    @RequirePermission(Perms.MANAGE_CATEGORIES)
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryDto dto) {
        ApiResponse response = categoryService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update category
     * PUT /api/v1/admin/categories/{id}
     */
    @RequirePermission(Perms.MANAGE_CATEGORIES)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer id,
            @RequestBody @Valid CategoryDto dto) {
        
        ApiResponse response = categoryService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete category
     * DELETE /api/v1/admin/categories/{id}
     */
    @RequirePermission(Perms.MANAGE_CATEGORIES)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        ApiResponse response = categoryService.delete(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Reorder categories
     * PUT /api/v1/admin/categories/reorder
     */
    @RequirePermission(Perms.MANAGE_CATEGORIES)
    @PutMapping("/reorder")
    public ResponseEntity<?> reorderCategories(@RequestBody CategoryReorderDto dto) {
        ApiResponse response = categoryService.reorder(dto);
        return ResponseEntity.ok(response);
    }
}
