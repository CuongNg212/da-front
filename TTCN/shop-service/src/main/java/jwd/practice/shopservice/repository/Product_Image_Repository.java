package jwd.practice.shopservice.repository;


import jwd.practice.shopservice.entity.Product;
import jwd.practice.shopservice.entity.Product_Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Product_Image_Repository extends JpaRepository<Product_Image, Integer> {
    List<Product_Image> findAllByProduct(Product product);
    void deleteProduct_ImagesByProduct(Product product);
}
