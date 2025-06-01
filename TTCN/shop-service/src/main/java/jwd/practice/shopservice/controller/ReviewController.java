package jwd.practice.shopservice.controller;

import jwd.practice.shopservice.dto.ReviewDTO;
import jwd.practice.shopservice.dto.response.ApiResponse;
import jwd.practice.shopservice.dto.response.ReviewResponse;
import jwd.practice.shopservice.entity.Review;
import jwd.practice.shopservice.service.Service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/review")
public class ReviewController {
    ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> addReview(@RequestParam int userId, @RequestParam int productId, @RequestBody ReviewDTO review) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.addReviewToProduct(userId, productId, review))
                .code(200)
                .build();

    }

    @GetMapping("/count")
    public ApiResponse<Integer> getCount(@RequestParam int productId) {
        return ApiResponse.<Integer>builder()
                .result(reviewService.getReviewsCountByProductId(productId))
                .code(200)
                .build();
    }

    @GetMapping("/average")
    public ApiResponse<Double> getAverage(@RequestParam int productId) {
        return ApiResponse.<Double>builder()
                .result(reviewService.getAverageRatingByProductId(productId))
                .code(200)
                .build();
    }

    @GetMapping
    public ApiResponse<List<Review>> getReviewByProductId(@RequestParam int productId) {
        return ApiResponse.<List<Review>>builder()
                .result(reviewService.getAllReviewsByProductId(productId))
                .code(200)
                .build();
    }


    @DeleteMapping
    public ApiResponse<Void> deleteReviewById(@RequestParam int reviewId) {
        reviewService.deleteReviewById(reviewId);
        return null;
    }

    @PutMapping
    public ApiResponse<ReviewResponse> updateReviewById(@RequestParam int reviewId, @RequestBody ReviewDTO review) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.updateReviewById(reviewId, review))
                .code(200)
                .build();
    }


}
