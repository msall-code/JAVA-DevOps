package com.groupeisi.company.service;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.entities.Achats;
import com.groupeisi.company.entities.Produits;
import com.groupeisi.company.mapper.AchatsMapper;
import com.groupeisi.company.repository.AchatsRepository;
import com.groupeisi.company.repository.ProduitsRepository;
import com.groupeisi.company.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchatsService {

    private final AchatsRepository achatsRepository;
    private final ProduitsRepository produitsRepository;
    private final UserAccountRepository userAccountRepository;
    private final AchatsMapper achatsMapper;

    public List<AchatsDto> findAll() {
        log.info("Récupération de tous les achats");
        return achatsMapper.toDtoList(achatsRepository.findAll());
    }

    @Cacheable(value = "achats", key = "#id")
    public AchatsDto findById(Long id) {
        log.info("Récupération de l'achat id={}", id);
        return achatsMapper.toDto(achatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Achat introuvable : " + id)));
    }

    public List<AchatsDto> findByUser(Long userId) {
        return achatsMapper.toDtoList(achatsRepository.findByUserId(userId));
    }

    public List<AchatsDto> findByProduct(String productRef) {
        return achatsMapper.toDtoList(achatsRepository.findByProductRef(productRef));
    }

    @CacheEvict(value = {"achats", "produits"}, allEntries = true)
    public AchatsDto create(AchatsDto dto) {
        log.info("Création d'un achat pour le produit ref={}",
                dto.getProductRef());

        // Récupération du produit
        Produits produit = produitsRepository.findById(dto.getProductRef())
                .orElseThrow(() -> new RuntimeException(
                        "Produit introuvable : " + dto.getProductRef()));

        // Incrémentation du stock
        produit.setStock(produit.getStock() + dto.getQuantity());
        produitsRepository.save(produit);
        log.info("Stock incrémenté pour {} : nouveau stock = {}",
                produit.getRef(), produit.getStock());

        // Création de l'achat
        Achats achat = achatsMapper.toEntity(dto);
        achat.setDateP(new Date());
        achat.setVenteValidee(false);
        achat.setProduct(produit);
        achat.setUser(userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable : " + dto.getUserId())));

        return achatsMapper.toDto(achatsRepository.save(achat));
    }

    @CacheEvict(value = "achats", allEntries = true)
    public AchatsDto update(Long id, AchatsDto dto) {
        log.info("Mise à jour de l'achat id={}", id);

        Achats existing = achatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Achat introuvable : " + id));

        existing.setQuantity(dto.getQuantity());
        existing.setProduct(produitsRepository.findById(dto.getProductRef())
                .orElseThrow(() -> new RuntimeException(
                        "Produit introuvable : " + dto.getProductRef())));
        existing.setUser(userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable : " + dto.getUserId())));

        return achatsMapper.toDto(achatsRepository.save(existing));
    }

    @CacheEvict(value = "achats", allEntries = true)
    public void delete(Long id) {
        log.info("Suppression de l'achat id={}", id);

        if (!achatsRepository.existsById(id)) {
            throw new RuntimeException("Achat introuvable : " + id);
        }

        achatsRepository.deleteById(id);
    }
}