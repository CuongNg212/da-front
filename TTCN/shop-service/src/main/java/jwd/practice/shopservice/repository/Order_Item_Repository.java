package jwd.practice.shopservice.repository;

import jwd.practice.shopservice.entity.Order_Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Order_Item_Repository extends JpaRepository<Order_Item, Integer> {
}
