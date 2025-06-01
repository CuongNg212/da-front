package jwd.practice.shopservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    int categoryId;

    @Column(name = "category_name")
    String categoryName;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updateAt;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference("category-products")
    @JsonIgnore
    List<Product> products;

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


}