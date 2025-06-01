package jwd.practice.shopservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    int productId;
    String productName;
    String description;
    double price;
//    int stock;
//    int size;
//    String color;
    double discount;
    String material;
    String category;
    int soldQuantity;
    List<String> images;

}
