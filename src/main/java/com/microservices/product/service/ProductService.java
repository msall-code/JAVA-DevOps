package com.microservices.product.service;

import com.microservices.product.dto.*;
import com.microservices.product.entity.Product;
import com.microservices.product.exception.*;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
        log.info("Création produit : {}", request.getName());
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Mise à jour produit id={}", id);
        Product product = findOrThrow(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id))
            throw new ProductNotFoundException(id);
        productRepository.deleteById(id);
    }

    // ✅ Appelé par order-service via Feign (communication synchrone)
    public ProductResponse decrementStock(Long productId, int quantity) {
        log.info("Décrémentation stock produit id={}, quantité={}", productId, quantity);
        Product product = findOrThrow(productId);
        if (product.getStock() < quantity)
            throw new InsufficientStockException(productId, quantity, product.getStock());
        product.setStock(product.getStock() - quantity);
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsInStock() {
        return productRepository.findAllInStock()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Utilitaires ────────────────────────────────────────────────────────────
    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}