package jwd.practice.shopservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    int reviewId;
    int rating;
    String comment;
    Instant createdAt;
    int userId;
    int productId;
}
