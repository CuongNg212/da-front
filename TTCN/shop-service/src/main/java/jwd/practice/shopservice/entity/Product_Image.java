package jwd.practice.shopservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Table(name = "images")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product_Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference("product-productImages")
    Product product;

}