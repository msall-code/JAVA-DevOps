package com.microservices.order.repository;

import com.microservices.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Correction du paramètre : "Email" -> "customerEmail" pour matcher l'entité
    List<Order> findByCustomerEmail(String customerEmail);
}