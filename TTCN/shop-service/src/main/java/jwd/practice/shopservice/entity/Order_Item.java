package jwd.practice.shopservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jwd.practice.shopservice.entity.order.Order;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order_item")
public class Order_Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer orderItemId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "variant_id", nullable = false)
    ProductVariant productVariant;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "price")
    BigDecimal price;
}