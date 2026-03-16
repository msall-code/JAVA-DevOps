package com.groupeisi.company.repository;

import com.groupeisi.company.entities.Ventes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentesRepository extends JpaRepository<Ventes, Long> {

    List<Ventes> findByUserId(Long userId);
    List<Ventes> findByProductRef(String productRef);
}