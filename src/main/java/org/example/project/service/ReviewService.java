package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ReviewDto;
import org.example.project.entity.Product;
import org.example.project.entity.Review;
import org.example.project.entity.Users;
import org.example.project.exception.AlreadyExistException;
import org.example.project.exception.NotFoundException;
import org.example.project.extra.ApiResponse;
import org.example.project.repository.ProductRepo;
import org.example.project.repository.ReviewRepo;
import org.example.project.repository.UsersRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UsersRepo usersRepo;
    private final ProductRepo productRepo;
    private final ReviewRepo reviewRepo;

    private Users getUser(Authentication auth){
        return usersRepo.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public ApiResponse addReview(Authentication authentication, ReviewDto dto){
        Users user = getUser(authentication);
        Product product = productRepo.findById(dto.getProductId()).orElseThrow(() -> new NotFoundException("Product not found"));
        if (reviewRepo.existsByUserAndProduct(user , product)) {
            throw new AlreadyExistException("Siz allaqachon baho bergansiz");
        }

        Review review = Review.builder().comment(dto.getComment()).star(dto.getStar()).product(product).user(user).build();
        reviewRepo.save(review);
        return ApiResponse.builder().message("Review added").status(true).data(review).build();
    }

    public ApiResponse updateReview(Authentication authentication, Integer id, ReviewDto dto){
        Users user = getUser(authentication);
        Review review = reviewRepo.findById(id).orElseThrow(() -> new NotFoundException("Review not found"));
        if (!review.getUser().getId().equals(user.getId())) throw new NotFoundException("Review not found for user");
        review.setComment(dto.getComment());
        review.setStar(dto.getStar());
        reviewRepo.save(review);
        return ApiResponse.builder().message("Review updated").status(true).data(review).build();
    }

    public ApiResponse deleteReview(Authentication authentication, Integer id){
        Users user = getUser(authentication);
        Review review = reviewRepo.findById(id).orElseThrow(() -> new NotFoundException("Review not found"));
        if (!review.getUser().getId().equals(user.getId())) throw new NotFoundException("Review not found for user");
        reviewRepo.delete(review);
        return ApiResponse.builder().message("Review deleted").status(true).build();
    }

    public ApiResponse getReviewsByProduct(Integer productId){
        Product product = productRepo.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        List<Review> list = reviewRepo.findAllByProduct(product);
        return ApiResponse.builder().message("Reviews retrieved").status(true).data(list).build();
    }
}

