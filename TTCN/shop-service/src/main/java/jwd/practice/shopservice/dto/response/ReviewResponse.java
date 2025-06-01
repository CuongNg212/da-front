package jwd.practice.shopservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    int reviewId;
    int rating;
    String comment;
    Instant createdAt;
}
