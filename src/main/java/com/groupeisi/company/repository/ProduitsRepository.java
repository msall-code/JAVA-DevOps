package com.groupeisi.company.repository;

import com.groupeisi.company.entities.Produits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProduitsRepository extends JpaRepository<Produits, String> {

    List<Produits> findByUserId(Long userId);
    boolean existsByName(String name);
}