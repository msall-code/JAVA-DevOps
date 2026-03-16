package com.groupeisi.company.controller;

import com.groupeisi.company.dto.VentesDto;
import com.groupeisi.company.service.VentesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
@Tag(name = "Ventes", description = "Gestion des ventes")
public class VentesController {

    private final VentesService ventesService;

    @GetMapping
    @Operation(summary = "Lister toutes les ventes")
    public ResponseEntity<List<VentesDto>> findAll() {
        return ResponseEntity.ok(ventesService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une vente par ID")
    public ResponseEntity<VentesDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ventesService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les ventes d'un utilisateur")
    public ResponseEntity<List<VentesDto>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ventesService.findByUser(userId));
    }

    @GetMapping("/produit/{productRef}")
    @Operation(summary = "Lister les ventes d'un produit")
    public ResponseEntity<List<VentesDto>> findByProduct(@PathVariable String productRef) {
        return ResponseEntity.ok(ventesService.findByProduct(productRef));
    }

    @PostMapping
    @Operation(summary = "Créer une vente")
    public ResponseEntity<VentesDto> create(@RequestBody VentesDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventesService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une vente")
    public ResponseEntity<VentesDto> update(@PathVariable Long id,
                                            @RequestBody VentesDto dto) {
        return ResponseEntity.ok(ventesService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une vente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}