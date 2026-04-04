package com.microservices.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Le produit avec l'ID " + id + " n'existe pas.");
    }
}