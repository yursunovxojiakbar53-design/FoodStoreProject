package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ReviewDto;
import org.example.project.extra.ApiResponse;
import org.example.project.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> add(Authentication authentication, @RequestBody ReviewDto dto){
        ApiResponse apiResponse = reviewService.addReview(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication authentication, @PathVariable Integer id, @RequestBody ReviewDto dto){
        ApiResponse apiResponse = reviewService.updateReview(authentication, id, dto);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable Integer id){
        ApiResponse apiResponse = reviewService.deleteReview(authentication, id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> listByProduct(@PathVariable Integer productId){
        ApiResponse apiResponse = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(apiResponse);
    }
}

