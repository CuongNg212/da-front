package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariant_Repository extends JpaRepository<ProductVariant,Integer> {
    void deleteProductVariantsByProduct(Product product);
    List<ProductVariant> findProductVariantsByProduct(Product product);
//    ProductVariant findProductVariantsByProductAndColorAndSize(Product product,String color,int size);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product = :product AND pv.color = :color AND pv.size = :size")
    ProductVariant findProductVariantByProductAndColorAndSize(
            @Param("product") Product product,
            @Param("color") String color,
            @Param("size") int size);


//@Query("SELECT pv FROM ProductVariant pv WHERE pv.product = :product AND pv.color = :color AND pv.size = :size")
//Optional<ProductVariant> findByProductColorAndSize(
//        @Param("product") Product product,
//        @Param("color") String color,
//        @Param("size") int size);
//}

    @Query(value = "SELECT * FROM product_variant WHERE product_id = :productId AND color = :color AND size = :size", nativeQuery = true)
    ProductVariant findByProductColorAndSize(
            @Param("productId") int productId,
            @Param("color") String color,
            @Param("size") int size);

    @Query(value = "SELECT SUM(o.stock) " +
            "FROM shop.product_variant o " +
            "WHERE o.product_id = :product_id AND o.color = :color AND o.size = :size", nativeQuery = true)
    int getSumOfProduct(@Param("product_id") int product_id, @Param("color") String color, @Param("size") int size);

    @Query(value = "SELECT SUM(o.stock) " +
            "FROM shop.product_variant o " +
            "WHERE o.variant_id = :variant_id", nativeQuery = true)
    int getSumOfProductVariant(@Param("variant_id") int variant_id);

    @Query(value = "SELECT p.* FROM product p JOIN product_variant pv ON p.product_id = pv.product_id WHERE pv.variant_id = :variantId", nativeQuery = true)
    Product findProductByVariantId(@Param("variantId") int variantId);
}
