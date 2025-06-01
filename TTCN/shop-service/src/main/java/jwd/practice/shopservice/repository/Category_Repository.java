package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Category_Repository extends JpaRepository<Category, Integer> {
    Category findByCategoryName(String name);
}
