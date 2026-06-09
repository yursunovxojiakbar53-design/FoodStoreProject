package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.CategoryDto;
import org.example.project.dto.CategoryReorderDto;
import org.example.project.entity.Category;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.CategoryService;
import org.example.project.valid.RequirePermission;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@RequirePermission(Perms.MANAGE_CATEGORIES)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse add(@RequestBody CategoryDto dto) {
        return categoryService.add(dto);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Integer id, @RequestBody CategoryDto dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }

    @PutMapping("/reorder")
    public ApiResponse reorder(@RequestBody CategoryReorderDto dto) {
        return categoryService.reorder(dto);
    }

    @RequirePermission(Perms.VIEW_CATEGORIES)
    @GetMapping
    public Page<Category> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(page, size);
    }

    @RequirePermission(Perms.VIEW_CATEGORIES)
    @GetMapping("/{id}")
    public Category getOne(@PathVariable Integer id) {
        return categoryService.getOne(id);
    }
    @GetMapping("/open")
    public ApiResponse getAllOpen() {
        return categoryService.getAllOpen();
    }
}