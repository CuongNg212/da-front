package jwd.practice.shopservice.service.IService;



import jwd.practice.shopservice.dto.response.CartItemResponse;
import jwd.practice.shopservice.entity.Cart;

import java.util.List;
import java.util.UUID;

public interface ICartService {
    void addProductToCart(int userId, int productId, int size, String color, int quantity);


    List<CartItemResponse> getAllCartItem(int userId );

    void deleteCart(int userId);

    Integer getSumQuantity(int userId);

    Double getSumPrice(int userId);

    void deleteByVariantId(int userId, int variantId);

    Cart updateCart(int userId, Cart cart);

}
