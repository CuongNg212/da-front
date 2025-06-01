package jwd.practice.shopservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    int cartId;

    int sumQuantity; // tổng số lượng mặt hàng

    Double sumPrice;//tổng giá tiền

    int userId;

    @OneToMany(mappedBy = "cart")
    @JsonManagedReference("cart-cartItem") // Đặt tên duy nhất cho tham chiếu này
    List<Cart_Item> cartItem;

}