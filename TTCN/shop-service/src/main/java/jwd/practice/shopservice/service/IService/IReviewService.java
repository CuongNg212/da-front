package jwd.practice.shopservice.service.IService;




import jwd.practice.shopservice.dto.ReviewDTO;
import jwd.practice.shopservice.dto.response.ReviewResponse;
import jwd.practice.shopservice.entity.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IReviewService {
    ReviewResponse addReviewToProduct(int user_id, int product_id, ReviewDTO review);
    List<Review> getAllReviews();
    Optional<ReviewResponse> getReviewById(int id);
    void deleteReviewById(int id);
    ReviewResponse updateReviewById(int id, ReviewDTO review);
    List<Review> getAllReviewsByProductId(int product_id);
    int getReviewsCountByProductId(int product_id);
    double getAverageRatingByProductId(int product_id);
}
