package jwd.practice.shopservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    String categoryName;
    String description;
    Instant creatAt;
    Instant updateAt;
}
