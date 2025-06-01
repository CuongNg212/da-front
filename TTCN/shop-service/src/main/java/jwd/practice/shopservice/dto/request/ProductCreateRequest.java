package jwd.practice.shopservice.dto.request;


import jwd.practice.shopservice.dto.ProductVatriantDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateRequest {
    String productName;
    String description;
    double price;
    double discount;
    String material;
    String category;
    List<ProductVatriantDTO> variants; // danh sach ca bien the
}
