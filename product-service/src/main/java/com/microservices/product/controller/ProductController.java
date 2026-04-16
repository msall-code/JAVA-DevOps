package com.microservices.product.controller;

import com.microservices.product.dto.*;
import com.microservices.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Note: En prod, remplace par l'URL exacte de ton front
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Plus propre pour un POST
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        log.info("Creating product: {}", request.getName());
        return productService.createProduct(request);
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("Updating product ID: {}", id);
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting product ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint spécial pour la communication inter-services (ex: Service Commande)
    @PutMapping("/{id}/stock/decrement")
    public ResponseEntity<ProductResponse> decrementStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        log.info("📥 [STOCK] Décrémentation pour ID: {}, quantité: {}", id, request.getQuantity());
        return ResponseEntity.ok(productService.decrementStock(id, request.getQuantity()));
    }
}