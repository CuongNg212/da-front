package jwd.practice.shopservice.dto.response;



import com.fasterxml.jackson.annotation.JsonInclude;
import jwd.practice.shopservice.entity.order.OrderStatus;
import jwd.practice.shopservice.entity.order.PaymentMethod;
import jwd.practice.shopservice.entity.order.PaymentStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    Integer id;

    int userId;

    String code;

    BigDecimal totalAmount;

    BigDecimal discount_voucher;

    BigDecimal totalPrice;

    OrderStatus status;

    PaymentMethod paymentMethod;

    PaymentStatus paymentStatus;

    String recipientName;

    String recipientPhone;

    String recipientAddress;

    String description;

    Timestamp createdAt;

    Timestamp updatedAt;

    List<Order_Item_Response> orderItems;
}
