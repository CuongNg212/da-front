package jwd.practice.shopservice.controller;


import jwd.practice.shopservice.dto.response.CartItemResponse;
import jwd.practice.shopservice.dto.response.UserResponse;
import jwd.practice.shopservice.entity.Cart;
import jwd.practice.shopservice.mapper.httpClient.AuthClient;
import jwd.practice.shopservice.service.Service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class CartController {
    CartService cartService;
    AuthClient authClient;

    // thêm một sản phẩm vào giỏ hàng
    @PostMapping("/cart")
    @Transactional
    public ResponseEntity<String> addProductToCart(@RequestParam(name = "userId") int userId,
                                                   @RequestParam(name = "productId") int productId, @RequestParam(name = "quantity") int quantity,
                                                   @RequestParam(name = "size") int size, @RequestParam(name = "color") String color)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication : "+authentication);
        UserResponse profile = this.authClient.getUserById(userId).getResult();
        if (profile == null)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi thêm vào giỏ hàng: id người dùng không tồn tại" );
        }
        try {
            this.cartService.addProductToCart(userId, productId,size, color, quantity);
            return ResponseEntity.ok("Thêm thành công vào giỏ hàng");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi thêm vào giỏ hàng: " + e.getMessage());
        }
    }
    @GetMapping("/cart")
    public ResponseEntity<List<CartItemResponse>> getAllCartItem(@RequestParam int userId)
    {
        return ResponseEntity.ok(this.cartService.getAllCartItem(userId));
    }

    @PutMapping("/cart/update")
    @Transactional
    public ResponseEntity<Cart> updateCartItem(@RequestParam(name = "userId") int userId, @RequestBody Cart cart){
        return ResponseEntity.ok(cartService.updateCart(userId, cart));

    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> deleteCart(@RequestParam(name = "userId") int userId)
    {
        try {
            this.cartService.deleteCart( userId);
            return ResponseEntity.ok("xoa thành công giỏ hàng của người dùng có id "+ userId);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("xay ra loi: "+ e.getMessage());
        }
    }
    @GetMapping("/cart/quantity")
    public ResponseEntity<Integer> getSumQuantity(@RequestParam int userId)
    {
        return ResponseEntity.ok(this.cartService.getSumQuantity(userId) );
    }
    @DeleteMapping("cart/variant/{variantId}")
    public ResponseEntity<String> deleteById(@RequestParam(name = "userId") int userId, @PathVariable int variantId)
    {
        try {
            this.cartService.deleteByVariantId(userId,variantId);
            return ResponseEntity.ok("success");
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(e.getMessage());
        }
    }
    @GetMapping("/cart/sumprice")
    public ResponseEntity<Double> getSumPrice(@RequestParam int userId)
    {
        return ResponseEntity.ok(this.cartService.getSumPrice(userId));
    }

}
