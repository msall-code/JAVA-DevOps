package com.groupeisi.company.controller;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.service.AchatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/achats")
@RequiredArgsConstructor
@Tag(name = "Achats", description = "Gestion des achats")
public class AchatsController {

    private final AchatsService achatsService;

    @GetMapping
    @Operation(summary = "Lister tous les achats")
    public ResponseEntity<List<AchatsDto>> findAll() {
        return ResponseEntity.ok(achatsService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un achat par ID")
    public ResponseEntity<AchatsDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(achatsService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les achats d'un utilisateur")
    public ResponseEntity<List<AchatsDto>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(achatsService.findByUser(userId));
    }

    @GetMapping("/produit/{productRef}")
    @Operation(summary = "Lister les achats d'un produit")
    public ResponseEntity<List<AchatsDto>> findByProduct(@PathVariable String productRef) {
        return ResponseEntity.ok(achatsService.findByProduct(productRef));
    }

    @PostMapping
    @Operation(summary = "Créer un achat")
    public ResponseEntity<AchatsDto> create(@RequestBody AchatsDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(achatsService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un achat")
    public ResponseEntity<AchatsDto> update(@PathVariable Long id,
                                            @RequestBody AchatsDto dto) {
        return ResponseEntity.ok(achatsService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un achat")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        achatsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}