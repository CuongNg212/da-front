package jwd.practice.shopservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jwd.practice.shopservice.entity.order.PaymentMethod;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderUpdateUser {

    String code;

    PaymentMethod paymentMethod;

    @NotBlank(message = "Tên người nhận không được để trống")
    String recipientName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải là 10 chữ số")
    String recipientPhone;

    @NotBlank(message = "Địa chỉ người nhận không được để trống")
    String recipientAddress;

    String description;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    List<Order_Items_Create> orderItems;

}
