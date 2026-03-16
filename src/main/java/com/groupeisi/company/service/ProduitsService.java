package com.groupeisi.company.service;

import com.groupeisi.company.dto.ProduitsDto;
import com.groupeisi.company.entities.Produits;
import com.groupeisi.company.mapper.ProduitsMapper;
import com.groupeisi.company.repository.ProduitsRepository;
import com.groupeisi.company.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduitsService {

    private final ProduitsRepository produitsRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProduitsMapper produitsMapper;

    @Cacheable("produits")
    public List<ProduitsDto> findAll() {
        log.info("Récupération de tous les produits");
        return produitsMapper.toDtoList(produitsRepository.findAll());
    }

    @Cacheable(value = "produits", key = "#ref")
    public ProduitsDto findByRef(String ref) {
        log.info("Récupération du produit ref={}", ref);
        Produits produit = produitsRepository.findById(ref)
                .orElseThrow(() -> {
                    log.error("Produit ref={} introuvable", ref);
                    return new RuntimeException("Produit introuvable : " + ref);
                });
        return produitsMapper.toDto(produit);
    }

    public List<ProduitsDto> findByUser(Long userId) {
        log.info("Récupération des produits du user id={}", userId);
        return produitsMapper.toDtoList(produitsRepository.findByUserId(userId));
    }

    @CacheEvict(value = "produits", allEntries = true)
    public ProduitsDto create(ProduitsDto dto) {
        log.info("Création du produit ref={}", dto.getRef());
        if (produitsRepository.existsById(dto.getRef())) {
            throw new RuntimeException("Référence déjà utilisée : " + dto.getRef());
        }
        Produits produit = produitsMapper.toEntity(dto);
        produit.setUser(userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + dto.getUserId())));
        return produitsMapper.toDto(produitsRepository.save(produit));
    }

    @CacheEvict(value = "produits", allEntries = true)
    public ProduitsDto update(String ref, ProduitsDto dto) {
        log.info("Mise à jour du produit ref={}", ref);
        Produits existing = produitsRepository.findById(ref)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + ref));
        existing.setName(dto.getName());
        existing.setStock(dto.getStock());
        if (dto.getUserId() != null) {
            existing.setUser(userAccountRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + dto.getUserId())));
        }
        return produitsMapper.toDto(produitsRepository.save(existing));
    }

    @CacheEvict(value = "produits", allEntries = true)
    public void delete(String ref) {
        log.info("Suppression du produit ref={}", ref);
        if (!produitsRepository.existsById(ref)) {
            throw new RuntimeException("Produit introuvable : " + ref);
        }
        produitsRepository.deleteById(ref);
    }
}