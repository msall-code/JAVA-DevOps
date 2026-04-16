package com.microservices.product.service;

import com.microservices.product.dto.*;
import com.microservices.product.entity.Product;
import com.microservices.product.exception.*;
import com.microservices.product.mapper.ProductMapper;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductRequest request) {
        log.info("Création d'un nouveau produit : {}", request.getName());
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    // AJOUT : Cette méthode était appelée par ton Controller
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsInStock() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStock() > 0)
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(@NonNull Long id) {
        return productMapper.toResponse(findOrThrow(id));
    }

    public ProductResponse updateProduct(@NonNull Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return productMapper.toResponse(productRepository.save(product));
    }

    // AJOUT : Cette méthode était appelée par ton Controller
    public void deleteProduct(@NonNull Long id) {
        log.info("Suppression du produit ID : {}", id);
        Product product = findOrThrow(id);
        productRepository.delete(product);
    }

    public ProductResponse decrementStock(@NonNull Long productId, int quantity) {
        Product product = findOrThrow(productId);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, product.getStock(), quantity);
        }
        product.setStock(product.getStock() - quantity);
        return productMapper.toResponse(productRepository.save(product));
    }

    private Product findOrThrow(@NonNull Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}