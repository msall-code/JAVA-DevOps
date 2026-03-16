package com.groupeisi.company.repository;

import com.groupeisi.company.entities.Achats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchatsRepository extends JpaRepository<Achats, Long> {

    List<Achats> findByUserId(Long userId);
    List<Achats> findByProductRef(String productRef);

}