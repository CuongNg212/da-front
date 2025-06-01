package jwd.practice.shopservice.dto.request;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order_Items_Create {
    int productVariantId;
    Integer quantity;
}
