package jwd.practice.shopservice.repository;


import jwd.practice.shopservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface Cart_Repository extends JpaRepository<Cart, Integer> {
    Cart findByUserId(int userOd);

    void deleteByUserId(int userId);
}
