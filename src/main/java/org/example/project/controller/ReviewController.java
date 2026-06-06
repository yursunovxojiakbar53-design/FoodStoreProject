package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ReviewDto;
import jakarta.validation.Valid;
import org.example.project.extra.ApiResponse;
import org.example.project.extra.Perms;
import org.example.project.service.ReviewService;
import org.example.project.valid.RequirePermission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @RequirePermission(Perms.MANAGE_OWN_REVIEW)
    @PostMapping
    public ResponseEntity<?> add(Authentication authentication, @RequestBody @Valid ReviewDto dto){
        ApiResponse apiResponse = reviewService.addReview(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_OWN_REVIEW)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(Authentication authentication, @PathVariable Integer id, @RequestBody @Valid ReviewDto dto){
        ApiResponse apiResponse = reviewService.updateReview(authentication, id, dto);
        return ResponseEntity.ok(apiResponse);
    }

    @RequirePermission(Perms.MANAGE_OWN_REVIEW)
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

