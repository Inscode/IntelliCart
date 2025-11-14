package com.insaf.ecommerce.repository;

import com.insaf.ecommerce.model.Cart;
import com.insaf.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
