package com.microservices.product.repository;

import com.microservices.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Requête personnalisée pour le filtrage
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAllInStock();
}