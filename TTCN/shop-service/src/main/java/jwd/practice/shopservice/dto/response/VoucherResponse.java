package jwd.practice.shopservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jwd.practice.shopservice.entity.voucher.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.sql.Date;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {
    String code;
    DiscountType discountType;
    BigDecimal discountValue;
    String description;
    BigDecimal minOrderValue;
    BigDecimal maxDiscount;
    Date startDate;
    Date endDate;
}
