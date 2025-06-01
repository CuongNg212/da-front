package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.Cart;
import jwd.practice.shopservice.entity.Cart_Item;
import jwd.practice.shopservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Cart_Item_Repository extends JpaRepository<Cart_Item, Integer> {
    Cart_Item findByCartAndProductVariant(Cart cart, ProductVariant productVariant);
    List<Cart_Item> findCart_ItemsByCart(Cart cart);

    void deleteByCart(Cart cartDelete);
    void deleteByProductVariant(ProductVariant productVariant);
    void deleteByCartAndProductVariant(Cart cart, ProductVariant productVariant);
}
