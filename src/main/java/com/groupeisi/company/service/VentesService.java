package com.groupeisi.company.service;

import com.groupeisi.company.dto.VentesDto;
import com.groupeisi.company.entities.Achats;
import com.groupeisi.company.entities.Produits;
import com.groupeisi.company.entities.Ventes;
import com.groupeisi.company.mapper.VentesMapper;
import com.groupeisi.company.repository.AchatsRepository;
import com.groupeisi.company.repository.ProduitsRepository;
import com.groupeisi.company.repository.UserAccountRepository;
import com.groupeisi.company.repository.VentesRepository;
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
public class VentesService {

    private final VentesRepository ventesRepository;
    private final ProduitsRepository produitsRepository;
    private final UserAccountRepository userAccountRepository;
    private final VentesMapper ventesMapper;
    private final AchatsRepository achatsRepository;

    @Cacheable("ventes")
    public List<VentesDto> findAll() {
        log.info("Récupération de toutes les ventes");
        return ventesMapper.toDtoList(ventesRepository.findAll());
    }

    @Cacheable(value = "ventes", key = "#id")
    public VentesDto findById(Long id) {
        log.info("Récupération de la vente id={}", id);
        return ventesMapper.toDto(ventesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Vente introuvable : " + id)));
    }

    public List<VentesDto> findByUser(Long userId) {
        return ventesMapper.toDtoList(ventesRepository.findByUserId(userId));
    }

    public List<VentesDto> findByProduct(String productRef) {
        return ventesMapper.toDtoList(ventesRepository.findByProductRef(productRef));
    }

    @CacheEvict(value = {"ventes", "produits"}, allEntries = true)
    public VentesDto create(VentesDto dto) {
        log.info("Création d'une vente pour le produit ref={}", dto.getProductRef());

        Produits produit = produitsRepository.findById(dto.getProductRef())
                .orElseThrow(() -> new RuntimeException(
                        "Produit introuvable : " + dto.getProductRef()));

        if (produit.getStock() < dto.getQuantity()) {
            throw new RuntimeException(
                    "Stock insuffisant ! Disponible : " + produit.getStock()
                            + ", demandé : " + dto.getQuantity());
        }

        produit.setStock(produit.getStock() - dto.getQuantity());
        produitsRepository.save(produit);
        log.info("Stock décrémenté pour {} : nouveau stock = {}",
                produit.getRef(), produit.getStock());

        Ventes vente = ventesMapper.toEntity(dto);
        vente.setDateP(new Date());
        vente.setProduct(produit);
        vente.setUser(userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable : " + dto.getUserId())));

        return ventesMapper.toDto(ventesRepository.save(vente));
    }

    @CacheEvict(value = {"ventes", "produits", "achats"}, allEntries = true)
    public VentesDto createFromAchat(Long achatId) {
        log.info("Création d'une vente depuis l'achat id={}", achatId);

        Achats achat = achatsRepository.findById(achatId)
                .orElseThrow(() -> new RuntimeException(
                        "Achat introuvable : " + achatId));

        // Vérification si déjà validé
        if (Boolean.TRUE.equals(achat.getVenteValidee())) {
            throw new RuntimeException(
                    "La vente de cet achat a déjà été validée !");
        }

        Produits produit = achat.getProduct();

        if (produit.getStock() < achat.getQuantity()) {
            throw new RuntimeException(
                    "Stock insuffisant ! Disponible : " + produit.getStock()
                            + ", demandé : " + achat.getQuantity());
        }

        // Décrémentation du stock
        produit.setStock(produit.getStock() - achat.getQuantity());
        produitsRepository.save(produit);
        log.info("Stock décrémenté pour {} : nouveau stock = {}",
                produit.getRef(), produit.getStock());

        // Marquer l'achat comme validé
        achat.setVenteValidee(true);
        achatsRepository.save(achat);

        // Création de la vente
        Ventes vente = new Ventes();
        vente.setDateP(new Date());
        vente.setQuantity(achat.getQuantity());
        vente.setProduct(produit);
        vente.setUser(achat.getUser());

        return ventesMapper.toDto(ventesRepository.save(vente));
    }

    @CacheEvict(value = {"ventes", "produits"}, allEntries = true)
    public VentesDto update(Long id, VentesDto dto) {
        log.info("Mise à jour de la vente id={}", id);

        Ventes existing = ventesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Vente introuvable : " + id));

        Produits ancienProduit = existing.getProduct();
        ancienProduit.setStock(ancienProduit.getStock() + existing.getQuantity());
        produitsRepository.save(ancienProduit);

        Produits nouveauProduit = produitsRepository.findById(dto.getProductRef())
                .orElseThrow(() -> new RuntimeException(
                        "Produit introuvable : " + dto.getProductRef()));

        if (nouveauProduit.getStock() < dto.getQuantity()) {
            ancienProduit.setStock(
                    ancienProduit.getStock() - existing.getQuantity());
            produitsRepository.save(ancienProduit);
            throw new RuntimeException(
                    "Stock insuffisant ! Disponible : " + nouveauProduit.getStock()
                            + ", demandé : " + dto.getQuantity());
        }

        nouveauProduit.setStock(nouveauProduit.getStock() - dto.getQuantity());
        produitsRepository.save(nouveauProduit);

        existing.setQuantity(dto.getQuantity());
        existing.setProduct(nouveauProduit);
        existing.setUser(userAccountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable : " + dto.getUserId())));

        return ventesMapper.toDto(ventesRepository.save(existing));
    }

    @CacheEvict(value = {"ventes", "produits"}, allEntries = true)
    public void delete(Long id) {
        log.info("Suppression de la vente id={}", id);

        Ventes vente = ventesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Vente introuvable : " + id));

        Produits produit = vente.getProduct();
        produit.setStock(produit.getStock() + vente.getQuantity());
        produitsRepository.save(produit);
        log.info("Stock restauré pour {} : nouveau stock = {}",
                produit.getRef(), produit.getStock());

        ventesRepository.deleteById(id);
    }
}