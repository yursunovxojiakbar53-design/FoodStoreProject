package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Category;
import org.example.project.extra.ApiResponse;
import org.example.project.service.SearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/withCategory")
    public ResponseEntity<?> searchWithCategory(@RequestParam String categoryName) {
        ApiResponse apiResponse = searchService.searchCategory(categoryName);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/withProduct")
    public ResponseEntity<?> searchWithProduct(@RequestParam String productName,@RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable=PageRequest.of(page, size);
        ApiResponse apiResponse = searchService.searchProduct(productName,pageable );
        return ResponseEntity.ok(apiResponse);
    }

}
