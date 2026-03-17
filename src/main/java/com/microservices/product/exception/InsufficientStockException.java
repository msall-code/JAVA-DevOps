package com.microservices.product.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format(
                "Stock insuffisant pour le produit %d. Demandé: %d, Disponible: %d",
                productId, requested, available
        ));
    }
}