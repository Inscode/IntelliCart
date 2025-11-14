package com.insaf.ecommerce.repository;

import com.insaf.ecommerce.model.Order;
import com.insaf.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
