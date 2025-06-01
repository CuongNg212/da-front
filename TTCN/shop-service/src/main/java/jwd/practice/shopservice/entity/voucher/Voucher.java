package jwd.practice.shopservice.entity.voucher;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer voucherId;

    @Column(name = "code", nullable = false, unique = true)
    String code;

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    BigDecimal discountValue;

    @Column(name = "description")
    String description;

    @Column(name = "usage_limit", nullable = false)
    Integer usageLimit;

    @Column(name = "min_order_value", nullable = false)
    BigDecimal minOrderValue; // giá trị tối thiểu của đơn hàng mà voucher có thể áp dụng

    @Column(name = "max_discount")
    BigDecimal maxDiscount; //  Quy định số tiền giảm tối đa

    @Column(name = "start_date", nullable = false)
    Date startDate;

    @Column(name = "end_date", nullable = false)
    Date endDate;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt; // Thời gian tạo

}