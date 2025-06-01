package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.dto.response.CartItemResponse;
import jwd.practice.shopservice.entity.*;
import jwd.practice.shopservice.mapper.ICartItemMapper;
import jwd.practice.shopservice.repository.*;
import jwd.practice.shopservice.service.IService.ICartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService implements ICartService {
    Cart_Repository cartRepository;
    Product_Repository productRepository;
    Cart_Item_Repository cartItemRepository;
    ICartItemMapper cartItemMapper;
    ProductVariant_Repository productVariantRepository;
    @Override
    @Transactional
    public void addProductToCart(int userId, int productId, int size, String color, int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated user: " + authentication.getName());

        // Kiểm tra xem user đã có giỏ hàng chưa
        Cart cart = this.cartRepository.findByUserId(userId);

        if (cart == null) {
            // Nếu chưa có, tạo mới giỏ hàng
            cart = Cart.builder()
                    .userId(userId)
                    .sumQuantity(0)
                    .sumPrice(0.0)
                    .build();
            cart = this.cartRepository.save(cart);
        }

        // Kiểm tra sản phẩm có tồn tại không
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Kiểm tra biến thể sản phẩm có tồn tại không
        ProductVariant productVariant = this.productVariantRepository.findByProductColorAndSize(productId, color, size);

        if (productVariant == null) {
            System.out.println("Product Variant not found for product ID: " + productId + ", color: " + color + ", size: " + size);
            return; // Thoát nếu không tìm thấy biến thể
        }

        // Kiểm tra xem biến thể sản phẩm đã có trong giỏ hàng chưa
        Cart_Item existingCartItem = this.cartItemRepository.findByCartAndProductVariant(cart, productVariant);

        if (existingCartItem == null) {
            // Nếu chưa có, tạo mới Cart_Item
            Cart_Item newCartItem = new Cart_Item();
            newCartItem.setCart(cart);
            newCartItem.setProductVariant(productVariant);
            newCartItem.setQuantity(quantity);
            this.cartItemRepository.save(newCartItem);
        } else {
            // Nếu đã có, cập nhật số lượng
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            this.cartItemRepository.save(existingCartItem);
        }

        // Cập nhật tổng số lượng và tổng giá tiền của giỏ hàng
        int newSumQuantity = cart.getSumQuantity() + quantity;
        double newSumPrice = cart.getSumPrice() + (product.getPrice() * quantity);

        cart.setSumQuantity(newSumQuantity);
        cart.setSumPrice(newSumPrice);
        this.cartRepository.save(cart);

        System.out.println("Product added to cart successfully.");
    }

    @Override
    public List<CartItemResponse> getAllCartItem(int userId) {
        Cart cart = this.cartRepository.findByUserId(userId);
        List<Cart_Item> cartItemList = this.cartItemRepository.findCart_ItemsByCart(cart);
        return this.cartItemMapper.toCartItemResponseList(cartItemList);
    }

    @Override
    @Transactional
    public void deleteCart(int userId) {
        Cart cartDelete = this.cartRepository.findByUserId(userId);
        this.cartItemRepository.deleteByCart(cartDelete);
        this.cartRepository.deleteByUserId(userId);

    }

    @Override
    public Integer getSumQuantity(int userId) {
        Cart currentCart = this.cartRepository.findByUserId(userId);
        return currentCart.getSumQuantity();
    }

    @Override
    public Double getSumPrice(int userId) {
        Cart currentCart = this.cartRepository.findByUserId(userId);
        return currentCart.getSumPrice();
    }

    @Transactional
    @Override
    public void deleteByVariantId(int userId, int variantId) {
        Cart currentCart = this.cartRepository.findByUserId(userId);
        ProductVariant productVariant = this.productVariantRepository.findById(variantId).get();
        Product product = productVariant.getProduct();
        this.cartItemRepository.deleteByCartAndProductVariant(currentCart,productVariant);
        currentCart.setSumPrice(currentCart.getSumPrice()-product.getPrice());
        currentCart.setSumQuantity(currentCart.getSumQuantity()-1);

        List<Cart_Item> cartItemList = this.cartItemRepository.findCart_ItemsByCart(currentCart);
        if(cartItemList.size() == 0)
            deleteCart(userId);
    }

    @Override
    public Cart updateCart(int userid, Cart cart) {
        Cart cartUpdate = cartRepository.findByUserId(userid);
        cartUpdate.setSumQuantity(cart.getSumQuantity());
        cartUpdate.setSumPrice(cart.getSumPrice());
        return cartRepository.save(cartUpdate);
    }
}
