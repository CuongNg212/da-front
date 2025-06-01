package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.dto.ReviewDTO;
import jwd.practice.shopservice.dto.response.ReviewResponse;
import jwd.practice.shopservice.dto.response.UserResponse;
import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.entity.Review;
import jwd.practice.shopservice.mapper.IReviewMapper;
import jwd.practice.shopservice.mapper.httpClient.AuthClient;
import jwd.practice.shopservice.repository.Product_Repository;
import jwd.practice.shopservice.repository.Review_Repository;
import jwd.practice.shopservice.service.IService.IReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService implements IReviewService {
    Review_Repository reviewRepository;
    Product_Repository productRepository;
    IReviewMapper reviewMapper;
    AuthClient authClient;

    public ReviewResponse addReviewToProduct(int user_id, int product_id, ReviewDTO review) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication : " + authentication.getName());

        // Gọi profile-service để kiểm tra user_id
        try {
            UserResponse userProfile = authClient.getUserById((user_id)).getResult();
        } catch (Exception e) {
            throw new RuntimeException("User ID " + user_id + " does not exist in profile-service.");
        }

        // Kiểm tra product_id trong database
        Product product = productRepository.findById(product_id)
                .orElseThrow(() -> new RuntimeException("Product ID " + product_id + " does not exist."));

        // Tạo review mới
        Review currentReview = new Review();
        currentReview.setRating(review.getRating());
        currentReview.setComment(review.getComment());
        currentReview.setProductId(product.getProductId());
        currentReview.setUserId(user_id);

        // Lưu review và trả về response
        return reviewMapper.toReviewResponse(reviewRepository.save(currentReview));
    }


    public List<Review> getAllReviews() {
        return reviewRepository.findAll().stream().toList();
    }

    public Optional<ReviewResponse> getReviewById(int id) {
        return reviewRepository.findById(id).map(reviewMapper::toReviewResponse);
    }

    public void deleteReviewById(int id) {
        reviewRepository.deleteById(id);
    }

    public ReviewResponse updateReviewById(int id, ReviewDTO review) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            reviewMapper.updateReview(optionalReview.get(), review);
            review.setRating(review.getRating());
            review.setComment(review.getComment());
        }
        return reviewMapper.toReviewResponse(reviewRepository.save(optionalReview.get()));
    }

    @Override
    public List<Review> getAllReviewsByProductId(int product_id) {
        if (productRepository.existsById(product_id)) {
            return reviewRepository.findByProductId(product_id);
        }
        return null;
    }

    @Override
    public int getReviewsCountByProductId(int product_id) {
        List<Review> reviews = getAllReviewsByProductId(product_id);
        int count = 0;
        for (int i = 0; i < reviews.size(); i++) {
            count++;
        }
        return count;
    }

    @Override
    public double getAverageRatingByProductId(int product_id) {
        try {
            int count = getReviewsCountByProductId(product_id);
            List<Review> reviews = getAllReviewsByProductId(product_id);
            int sum = 0;
            for (int i = 0; i < reviews.size(); i++) {
                sum += reviews.get(i).getRating();
            }
            return sum / count;
        } catch (Exception e) {
            return 0;
        }
    }
}
