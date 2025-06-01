package jwd.practice.shopservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImagesResponse {
    String imageUrl;
    int productId;
    int id;
}
