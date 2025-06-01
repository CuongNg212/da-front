package jwd.practice.shopservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    ProductVariantCart product;
    int quantity;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductVariantCart{
        int variantId;
        String productName;
        int productId;
        String color;
        double price;
        int size;
        int stock;
        List<String> images;
    }
}
