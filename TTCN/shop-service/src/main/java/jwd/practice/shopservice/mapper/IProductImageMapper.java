package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.response.ProductImagesResponse;
import jwd.practice.shopservice.entity.Product_Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IProductImageMapper {
    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "id",target = "id")
    ProductImagesResponse toProductImagesResponse(Product_Image productImage);
    List<ProductImagesResponse> toProductImagesResponseList(List<Product_Image> productImageList);
}
