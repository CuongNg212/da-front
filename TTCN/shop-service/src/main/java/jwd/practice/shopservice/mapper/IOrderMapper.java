package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.response.OrderResponse;
import jwd.practice.shopservice.dto.response.Order_Item_Response;
import jwd.practice.shopservice.entity.Order_Item;
import jwd.practice.shopservice.entity.Product_Image;
import jwd.practice.shopservice.entity.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IOrderMapper {

//
    List<OrderResponse> listOrderToOrderResponse (List<Order> orders);
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "voucher.code", target = "code")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "discount_voucher", target = "discount_voucher")
    OrderResponse orderToOrderResponse(Order order);
    // Map Order_Item to Order_Item_Response
    List<Order_Item_Response> orderItemsToOrderItemResponses(List<Order_Item> orderItems);
    @Mapping(source = "productVariant.variantId", target = "productVariantId")
    @Mapping(source = "productVariant.product.productId", target = "productId")
    @Mapping(source = "productVariant.product.productName", target = "productName")
    @Mapping(source = "productVariant.product.description", target = "description")
    @Mapping(source = "productVariant.product.price", target = "price_original")
    @Mapping(source = "productVariant.product.discount", target = "discount")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "productVariant.size", target = "size")
    @Mapping(source = "productVariant.color", target = "color")
    @Mapping(source = "productVariant.product.productImages", target = "image", qualifiedByName = "mapProductImagesToFirstImageUrl")
    Order_Item_Response orderItemToOrderItemResponse(Order_Item orderItem);

    @Named("mapProductImagesToFirstImageUrl")
    default String mapProductImagesToFirstImageUrl(List<Product_Image> productImages) {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.get(0).getImageUrl(); // Lấy ảnh đầu tiên trong danh sách
        }
        return null; // Trả về null nếu không có ảnh
    }
//

}
