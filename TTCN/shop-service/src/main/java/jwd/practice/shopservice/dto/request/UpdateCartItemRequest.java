package jwd.practice.shopservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCartItemRequest {
    private int variantId;
    private int quantity;
}
