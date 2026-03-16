package com.groupeisi.company.controller;

import com.groupeisi.company.dto.ProduitsDto;
import com.groupeisi.company.service.ProduitsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits")
public class ProduitsController {

    private final ProduitsService produitsService;

    @GetMapping
    @Operation(summary = "Lister tous les produits")
    public ResponseEntity<List<ProduitsDto>> findAll() {
        return ResponseEntity.ok(produitsService.findAll());
    }

    @GetMapping("/{ref}")
    @Operation(summary = "Trouver un produit par référence")
    public ResponseEntity<ProduitsDto> findByRef(@PathVariable String ref) {
        return ResponseEntity.ok(produitsService.findByRef(ref));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les produits d'un utilisateur")
    public ResponseEntity<List<ProduitsDto>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(produitsService.findByUser(userId));
    }

    @PostMapping
    @Operation(summary = "Créer un produit")
    public ResponseEntity<ProduitsDto> create(@RequestBody ProduitsDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produitsService.create(dto));
    }

    @PutMapping("/{ref}")
    @Operation(summary = "Modifier un produit")
    public ResponseEntity<ProduitsDto> update(@PathVariable String ref,
                                              @RequestBody ProduitsDto dto) {
        return ResponseEntity.ok(produitsService.update(ref, dto));
    }

    @DeleteMapping("/{ref}")
    @Operation(summary = "Supprimer un produit")
    public ResponseEntity<Void> delete(@PathVariable String ref) {
        produitsService.delete(ref);
        return ResponseEntity.noContent().build();
    }
}