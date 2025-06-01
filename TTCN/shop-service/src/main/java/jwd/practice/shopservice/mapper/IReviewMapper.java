package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.ReviewDTO;
import jwd.practice.shopservice.dto.response.ReviewResponse;
import jwd.practice.shopservice.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IReviewMapper {
    Review toReview(ReviewDTO reviewDTO);
    ReviewDTO toReviewDTO(Review review);
    ReviewResponse toReviewResponse(Review review);
    void updateReview(@MappingTarget Review review, ReviewDTO reviewDTO);
}
