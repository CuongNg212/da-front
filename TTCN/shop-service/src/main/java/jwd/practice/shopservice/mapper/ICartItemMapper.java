package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.response.CartItemResponse;
import jwd.practice.shopservice.entity.Cart_Item;
import jwd.practice.shopservice.entity.ProductVariant;
import jwd.practice.shopservice.entity.Product_Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ICartItemMapper {
//    @Mapping(target = "product.productName", source = "productVariant.product.productName")
//    @Mapping(target = "product.size", source = "productVariant.size")
//    @Mapping(target = "product.color", source = "productVariant.color")
//    @Mapping(target = "product.stock", source = "productVariant.stock")
//    @Mapping(target = "images", source = "productImages", qualifiedByName = "mapImages")
//    CartItemResponse toCartItemResponse(Cart_Item cartItem);
//    // Hàm ánh xạ từng `Product_Image` thành `String`
//    @Named("mapImages")
//    static List<String> mapImages(List<Product_Image> productImages) {
//        return productImages != null ?
//                productImages.stream().map(Product_Image::getImageUrl).collect(Collectors.toList()) :
//                null;
//    }

    @Mapping(target = "product", source = "productVariant", qualifiedByName = "toProductVariantCart")
    CartItemResponse toCartItemResponse(Cart_Item cartItem);

    @Named("toProductVariantCart")
    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "variantId", source = "variantId")
    @Mapping(target = "images", source = "product.productImages", qualifiedByName = "mapImages")
    CartItemResponse.ProductVariantCart toProductVariantCart(ProductVariant productVariant);

    @Named("mapImages")
    static List<String> mapImages(List<Product_Image> productImages) {
        return productImages != null ?
                productImages.stream().map(Product_Image::getImageUrl).collect(Collectors.toList()) :
                null;
    }

    List<CartItemResponse> toCartItemResponseList(List<Cart_Item> cartItemList);
}
