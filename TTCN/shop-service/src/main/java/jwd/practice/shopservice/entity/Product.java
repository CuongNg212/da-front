package jwd.practice.shopservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    int productId;

    @Column(name = "product_name")
    String productName;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "price")
    double price;

    @Column(name = "discount")
    double discount;

    @Column(name = "sold_quantity", columnDefinition = "int default 0")
    int soldQuantity; //

//    @Column(name = "size")
//    int size;
//
//    @Column(name = "color")
//    String color;
//
//    @Column(name = "stock")
//    int stock;

    @Column(name = "material")
    String material;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference("category-products")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @JsonManagedReference("product-productImages")
    List<Product_Image> productImages;


    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    List<ProductVariant> productVariants;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updateAt;


    @PrePersist
    public void handleBeforeCreate()
    {
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate()
    {
        this.updateAt = Instant.now();
    }

    @Column(name = "saleAt")
    LocalDateTime saleAt;

}