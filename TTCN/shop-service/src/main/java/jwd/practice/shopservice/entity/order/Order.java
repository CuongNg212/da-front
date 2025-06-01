package jwd.practice.shopservice.entity.order;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jwd.practice.shopservice.entity.Order_Item;
import jwd.practice.shopservice.entity.voucher.Voucher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int userId;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    @JsonBackReference
    Voucher voucher;

    @Column(name = "total_amount", nullable = false)
    BigDecimal totalAmount; // tổng giá trị của đơn hàng trước khi áp dụng voucher

    @Column(name = "discount_voucher")
    BigDecimal discount_voucher;

    @Column(name = "total_price")
    BigDecimal totalPrice; // tổng tổng giá trị của đơn hàng sau khi áp dụng voucher

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    @Column(name = "recipient_name", nullable = false)
    String recipientName;

    @Column(name = "recipient_phone", nullable = false)
    String recipientPhone;

    @Column(name = "recipient_address", nullable = false)
    String recipientAddress;

    @Column(name = "description")
    String description;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt; // bao gồm cả phần giây, phút, giờ, ngày, tháng, và năm

    @Column(name = "updated_at")
    Timestamp updatedAt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
            @JsonManagedReference
    List<Order_Item> orderItems;

    public void addOrderItem(Order_Item orderItem) {
        orderItems.add(orderItem);
    }
    @PrePersist
    public void prePersist() {
        if (this.status == null) this.status = OrderStatus.PENDING;
        if(this.paymentStatus == null)  this.paymentStatus = PaymentStatus.UNPAID;
        if(this.createdAt == null)  this.createdAt = new Timestamp(System.currentTimeMillis());
    }

}