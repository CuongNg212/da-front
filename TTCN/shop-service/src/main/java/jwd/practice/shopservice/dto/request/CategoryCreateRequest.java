package jwd.practice.shopservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateRequest {
    String categoryName;
    String description;
}
